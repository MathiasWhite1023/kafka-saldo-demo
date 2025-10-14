import os, json, time
from confluent_kafka import Producer
from dotenv import load_dotenv

load_dotenv()
BOOTSTRAP = os.getenv("BOOTSTRAP_SERVERS", "localhost:9092")
TOPIC = os.getenv("TOPIC", "contas")

p = Producer({'bootstrap.servers': BOOTSTRAP})

def atualizar_saldo(cliente_id: str, saldo: float):
    msg = {
        "cliente_id": cliente_id,
        "saldo": saldo,
        "timestamp": time.strftime("%Y-%m-%dT%H:%M:%S")
    }
    p.produce(TOPIC, key=str(cliente_id), value=json.dumps(msg))
    p.flush()
    print(f"Produzido -> key={cliente_id} value={msg}")

if __name__ == "__main__":
    # exemplo: enfileira mudanças de saldo
    atualizar_saldo("1", 200.0)
    time.sleep(0.5)
    atualizar_saldo("2", 50.0)
    time.sleep(0.5)
    atualizar_saldo("1", 150.0)  # cliente 1 fez saque
    print("Mensagens enviadas.")
