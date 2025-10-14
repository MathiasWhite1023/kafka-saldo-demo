import os, json, threading, time
from flask import Flask, jsonify
from flask_cors import CORS
from confluent_kafka import Consumer, KafkaException, TopicPartition
from dotenv import load_dotenv

load_dotenv()
BOOTSTRAP = os.getenv("BOOTSTRAP_SERVERS", "localhost:9092")
TOPIC = os.getenv("TOPIC", "contas")
GROUP = os.getenv("GROUP", "consulta-saldo")

app = Flask(__name__)
CORS(app)  # Habilitar CORS

# estado em memória: mapa cliente_id -> ultima mensagem (dict)
estado = {}
estado_lock = threading.Lock()

def rebuild_state():
    """Lê todo o tópico desde o início e reconstrói o estado atual."""
    c = Consumer({
        'bootstrap.servers': BOOTSTRAP,
        'group.id': GROUP + "-rebuild",
        'auto.offset.reset': 'earliest',
        'enable.partition.eof': True
    })

    # Assina a partição 0 a partir do offset 0 (útil para demo com 1 partição).
    # Em produção, usar assign por todas as partições do tópico.
    c.assign([TopicPartition(TOPIC, 0, 0)])

    print("Reconstruindo estado a partir do tópico...")
    while True:
        msg = c.poll(1.0)
        if msg is None:
            # chegamos ao fim por enquanto
            break
        if msg.error():
            # EOF ou erro
            from confluent_kafka import KafkaError
            if msg.error().code() == KafkaError._PARTITION_EOF:
                break
            else:
                print("Erro consumer:", msg.error())
                continue
        key = msg.key().decode('utf-8') if msg.key() else None
        value = json.loads(msg.value().decode('utf-8'))
        with estado_lock:
            estado[key] = value

    c.close()
    print("Reconstrução finalizada. Número de chaves:", len(estado))

def run_streaming_consumer():
    """Consumer contínuo que atualiza estado conforme chegam novas mensagens."""
    c = Consumer({
        'bootstrap.servers': BOOTSTRAP,
        'group.id': GROUP,
        'auto.offset.reset': 'latest'
    })
    c.subscribe([TOPIC])
    print("Consumer streaming iniciado, aguardando novas mensagens...")
    try:
        while True:
            msg = c.poll(1.0)
            if msg is None:
                continue
            if msg.error():
                print("Erro:", msg.error())
                continue
            key = msg.key().decode('utf-8') if msg.key() else None
            value = json.loads(msg.value().decode('utf-8'))
            with estado_lock:
                estado[key] = value
            print(f"Atualizado estado: {key} -> {value}")
    except KeyboardInterrupt:
        pass
    finally:
        c.close()

@app.route("/saldo/<cliente_id>", methods=["GET"])
def get_saldo(cliente_id):
    with estado_lock:
        data = estado.get(str(cliente_id))
    if data:
        return jsonify({"cliente_id": cliente_id, "saldo": data["saldo"], "timestamp": data["timestamp"]})
    else:
        return jsonify({"error": "cliente não encontrado"}), 404

@app.route("/saldos", methods=["GET"])
def get_all_saldos():
    with estado_lock:
        all_data = [{"cliente_id": k, "saldo": v["saldo"], "timestamp": v["timestamp"]} for k, v in estado.items()]
    return jsonify({"total": len(all_data), "clientes": all_data})

if __name__ == "__main__":
    # 1) Reconstruir estado no startup
    rebuild_state()
    # 2) Iniciar consumer que atualiza estado em background
    t = threading.Thread(target=run_streaming_consumer, daemon=True)
    t.start()
    # 3) Iniciar Flask (API) para consultas
    app.run(port=5001, debug=False, use_reloader=False)
