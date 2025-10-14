#!/usr/bin/env python3
"""
Producer Interativo - Kafka Saldo Demo
Permite atualizar saldos interativamente via terminal
"""

import os
import json
import time
import sys
from confluent_kafka import Producer
from dotenv import load_dotenv

load_dotenv()
BOOTSTRAP = os.getenv("BOOTSTRAP_SERVERS", "localhost:9092")
TOPIC = os.getenv("TOPIC", "contas")

p = Producer({'bootstrap.servers': BOOTSTRAP})

def atualizar_saldo(cliente_id: str, saldo: float):
    """Produz uma mensagem de atualização de saldo."""
    msg = {
        "cliente_id": cliente_id,
        "saldo": saldo,
        "timestamp": time.strftime("%Y-%m-%dT%H:%M:%S")
    }
    p.produce(TOPIC, key=str(cliente_id), value=json.dumps(msg))
    p.flush()
    print(f"✅ Produzido -> Cliente {cliente_id}: R$ {saldo:.2f}")

def main():
    print("=" * 50)
    print("📝 Producer Interativo - Kafka Saldo Demo")
    print("=" * 50)
    print()
    print("Digite os dados do cliente para atualizar o saldo.")
    print("Digite 'q' para sair.\n")

    while True:
        try:
            # Solicitar ID do cliente
            cliente_id = input("👤 ID do Cliente (ou 'q' para sair): ").strip()
            
            if cliente_id.lower() == 'q':
                print("\n👋 Encerrando producer...")
                break
            
            if not cliente_id:
                print("❌ ID do cliente não pode ser vazio!\n")
                continue
            
            # Solicitar saldo
            saldo_str = input("💰 Saldo (R$): ").strip()
            
            try:
                saldo = float(saldo_str)
            except ValueError:
                print("❌ Saldo inválido! Use números (ex: 150.50)\n")
                continue
            
            # Atualizar saldo
            atualizar_saldo(cliente_id, saldo)
            print()
            
        except KeyboardInterrupt:
            print("\n\n👋 Encerrando producer...")
            break
        except Exception as e:
            print(f"❌ Erro: {e}\n")

if __name__ == "__main__":
    main()
