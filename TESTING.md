# 🧪 Guia de Testes - Kafka Saldo Demo

Este documento descreve cenários de teste para validar o funcionamento do sistema.

## 🎯 Cenários de Teste

### 1. Teste Básico de Funcionamento

**Objetivo:** Verificar que o sistema está funcionando corretamente.

**Passos:**

1. Iniciar o sistema:
```bash
./start.sh
```

2. Consultar saldo do cliente 1:
```bash
curl http://localhost:5000/saldo/1
```

**Resultado Esperado:**
```json
{
  "cliente_id": "1",
  "saldo": 150.0,
  "timestamp": "2025-10-14T14:36:12"
}
```

---

### 2. Teste de Cliente Inexistente

**Objetivo:** Verificar o comportamento com cliente não cadastrado.

**Passos:**

1. Consultar cliente inexistente:
```bash
curl http://localhost:5000/saldo/999
```

**Resultado Esperado:**
```json
{
  "error": "cliente não encontrado"
}
```

**Status HTTP:** 404

---

### 3. Teste de Atualização em Tempo Real

**Objetivo:** Verificar que atualizações são refletidas imediatamente.

**Passos:**

1. Consultar saldo inicial:
```bash
curl http://localhost:5000/saldo/1
```

2. Atualizar saldo via producer interativo:
```bash
python producer_interactive.py
# Digite: 1
# Digite: 500.00
```

3. Consultar saldo novamente:
```bash
curl http://localhost:5000/saldo/1
```

**Resultado Esperado:** Saldo atualizado para 500.00

---

### 4. Teste de Compactação (Última Mensagem Vence)

**Objetivo:** Verificar que apenas a última mensagem por chave é mantida.

**Passos:**

1. Enviar múltiplas atualizações para o mesmo cliente:
```python
# No producer_interactive.py
# Cliente: 5
# Saldo: 100.00

# Cliente: 5
# Saldo: 200.00

# Cliente: 5
# Saldo: 300.00
```

2. Consultar saldo:
```bash
curl http://localhost:5000/saldo/5
```

**Resultado Esperado:** Saldo = 300.00 (última mensagem)

---

### 5. Teste de Reconstrução de Estado

**Objetivo:** Verificar que o estado é reconstruído corretamente após restart.

**Passos:**

1. Produzir algumas mensagens:
```bash
python producer.py
```

2. Parar o consumer service:
```bash
pkill -f "python.*consumer_service.py"
```

3. Reiniciar o consumer service:
```bash
nohup python consumer_service.py > consumer.log 2>&1 &
```

4. Aguardar 3 segundos e consultar:
```bash
sleep 3
curl http://localhost:5000/saldo/1
```

**Resultado Esperado:** Dados permanecem consistentes (estado reconstruído do tópico)

---

### 6. Teste de Performance (Múltiplas Consultas)

**Objetivo:** Verificar performance da API.

**Passos:**

1. Consultar múltiplas vezes rapidamente:
```bash
for i in {1..100}; do
  curl -s http://localhost:5000/saldo/1 > /dev/null
done
```

2. Verificar logs:
```bash
tail -f consumer.log
```

**Resultado Esperado:** Todas as requisições respondem rapidamente (< 100ms)

---

### 7. Teste de Múltiplos Clientes

**Objetivo:** Verificar que múltiplos clientes são gerenciados corretamente.

**Passos:**

1. Criar dados para 10 clientes:
```python
# Criar arquivo test_multiple.py
import os, json, time
from confluent_kafka import Producer
from dotenv import load_dotenv

load_dotenv()
p = Producer({'bootstrap.servers': 'localhost:9092'})

for i in range(1, 11):
    msg = {
        "cliente_id": str(i),
        "saldo": float(i * 100),
        "timestamp": time.strftime("%Y-%m-%dT%H:%M:%S")
    }
    p.produce('contas', key=str(i), value=json.dumps(msg))
    p.flush()
    print(f"Cliente {i}: R$ {i * 100}")
```

2. Executar:
```bash
python test_multiple.py
```

3. Consultar vários clientes:
```bash
for i in {1..10}; do
  echo "Cliente $i:"
  curl http://localhost:5000/saldo/$i
  echo ""
done
```

**Resultado Esperado:** Cada cliente tem o saldo correto (100, 200, 300, ..., 1000)

---

### 8. Teste de Formato JSON

**Objetivo:** Verificar que a API retorna JSON válido.

**Passos:**

1. Consultar e validar JSON:
```bash
curl -s http://localhost:5000/saldo/1 | python -m json.tool
```

**Resultado Esperado:** JSON formatado corretamente sem erros

---

### 9. Teste de Logs do Consumer

**Objetivo:** Verificar que logs estão sendo gerados corretamente.

**Passos:**

1. Ver logs em tempo real:
```bash
tail -f consumer.log
```

2. Produzir mensagem:
```bash
python producer.py
```

3. Verificar se aparece no log:
```
Atualizado estado: 1 -> {'cliente_id': '1', 'saldo': 150.0, ...}
```

**Resultado Esperado:** Mensagens aparecem no log

---

### 10. Teste do Tópico Kafka Diretamente

**Objetivo:** Verificar mensagens no tópico Kafka.

**Passos:**

1. Consumir mensagens do tópico:
```bash
docker-compose exec kafka kafka-console-consumer \
  --topic contas \
  --from-beginning \
  --bootstrap-server localhost:9092 \
  --property print.key=true \
  --property key.separator=":"
```

**Resultado Esperado:** Ver todas as mensagens (key:value)

---

## 🔧 Testes de Comandos Úteis

### Listar Tópicos

```bash
docker-compose exec kafka kafka-topics \
  --list \
  --bootstrap-server localhost:9092
```

### Descrever Tópico

```bash
docker-compose exec kafka kafka-topics \
  --describe \
  --topic contas \
  --bootstrap-server localhost:9092
```

**Verificar:** `cleanup.policy=compact`

### Verificar Consumer Groups

```bash
docker-compose exec kafka kafka-consumer-groups \
  --list \
  --bootstrap-server localhost:9092
```

### Ver Offset do Consumer

```bash
docker-compose exec kafka kafka-consumer-groups \
  --describe \
  --group consulta-saldo \
  --bootstrap-server localhost:9092
```

---

## 🐛 Troubleshooting

### API não responde

1. Verificar se o serviço está rodando:
```bash
ps aux | grep consumer_service
```

2. Verificar logs:
```bash
cat consumer.log
```

3. Verificar porta:
```bash
lsof -i :5000
```

### Kafka não inicia

1. Verificar containers:
```bash
docker ps -a
```

2. Ver logs do Kafka:
```bash
docker-compose logs kafka
```

3. Reiniciar:
```bash
docker-compose down
docker-compose up -d
```

### Mensagens não aparecem

1. Verificar se tópico existe:
```bash
docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092
```

2. Verificar se producer está funcionando:
```bash
python producer.py
# Deve mostrar: "Produzido -> key=..."
```

3. Verificar consumer logs:
```bash
tail -f consumer.log
```

---

## ✅ Checklist de Validação

- [ ] Sistema inicia com `./start.sh`
- [ ] API responde em `http://localhost:5000`
- [ ] Consulta `/saldo/1` retorna dados corretos
- [ ] Consulta `/saldo/999` retorna 404
- [ ] Producer interativo funciona
- [ ] Atualizações refletem na API
- [ ] Estado é reconstruído após restart
- [ ] Logs estão sendo gerados
- [ ] Tópico está configurado como compactado
- [ ] Sistema para com `./stop.sh`

---

**Última Atualização:** 14 de outubro de 2025
