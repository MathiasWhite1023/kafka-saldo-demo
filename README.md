# 🐍 Kafka Saldo Demo - Versão Python

Sistema de consulta de saldos bancários usando **Apache Kafka com tópico compactado** como fonte única de verdade.

> **💡 Esta é a implementação em Python com Flask.**  
> Para a versão Java/Spring Boot, veja a branch [`java-version`](../../tree/java-version)

---

## 📚 Índice

- [Por Que Kafka?](#-por-que-kafka)
- [Como Funciona](#-como-funciona)
- [Tecnologias Usadas](#-tecnologias-usadas)
- [Instalação](#-instalação)
- [Como Usar](#-como-usar)
- [Testando](#-testando)
- [Entendendo o Código](#-entendendo-o-código)

---

## 🎯 Por Que Kafka?

### Problema: Como manter saldos bancários sempre atualizados?

**Abordagem tradicional (banco de dados):**
```
Cliente 1: R$ 100,00  ← valor atual
Cliente 2: R$ 50,00   ← valor atual
```
❌ Não há histórico de mudanças  
❌ Se o banco cair, perdemos o estado  
❌ Difícil de replicar para múltiplos serviços  

**Abordagem com Kafka (Event Sourcing):**
```
Tópico 'contas' (compactado):
  cliente_1 → {"saldo": 200.0}  ← substituído
  cliente_1 → {"saldo": 150.0}  ← última versão (mantida)
  cliente_2 → {"saldo": 50.0}   ← última versão (mantida)
```
✅ Log de eventos como fonte única de verdade  
✅ Compactação mantém apenas último estado  
✅ Qualquer serviço pode reconstruir o estado completo  
✅ Alta disponibilidade e tolerância a falhas  

### 🔑 Conceito Principal: Tópico Compactado

Kafka com **log compaction** garante:
1. **Apenas a última mensagem** por chave (cliente_id) é mantida
2. **Reconstrução completa** do estado lendo o tópico do início
3. **Atualizações em tempo real** conforme novas mensagens chegam

**Exemplo:**
```python
# Envio 3 mensagens para o mesmo cliente:
producer.send(key="1", value={"saldo": 100.0})  # Será removida
producer.send(key="1", value={"saldo": 200.0})  # Será removida
producer.send(key="1", value={"saldo": 150.0})  # ✅ Esta fica!

# Ao consumir o tópico, só veremos a última:
consumer.poll() # → {"cliente_id": "1", "saldo": 150.0}
```

---

## 🏗️ Como Funciona

### Arquitetura do Sistema

```
┌─────────────────┐
│  Producer       │  1) Envia atualizações de saldo
│  (Python CLI)   │     com cliente_id como chave
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│  Kafka Topic: "contas" (compacted)      │
│  ┌─────────────────────────────────┐   │
│  │ key="1" → {"saldo": 150.0}      │   │  2) Kafka mantém
│  │ key="2" → {"saldo": 50.0}       │   │     apenas última
│  └─────────────────────────────────┘   │     mensagem/chave
└────────┬────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────┐
│  Consumer Service (Flask)               │
│  ┌───────────────────────────────────┐  │
│  │  1. Startup:                      │  │  3) Reconstrói
│  │     - Lê tópico do início         │  │     estado em
│  │     - Reconstrói estado em dict   │  │     memória
│  │                                   │  │
│  │  2. Runtime:                      │  │  4) Atualiza
│  │     - Escuta novas mensagens      │  │     estado em
│  │     - Atualiza dict em memória    │  │     tempo real
│  └───────────────────────────────────┘  │
│                                          │
│  API REST (Flask):                       │
│    GET /saldo/{id}  → Consulta saldo    │  5) Expõe API
│    GET /saldos      → Lista todos       │     para consulta
└──────────────────────────────────────────┘
```

### Fluxo de Dados

**1. Producer envia atualização:**
```python
# producer.py ou producer_interactive.py
producer.send(
    topic='contas',
    key='1',                          # cliente_id
    value='{"cliente_id":"1","saldo":150.0,"timestamp":"..."}'
)
```

**2. Kafka armazena e compacta:**
- Mensagens com mesma chave (cliente_id) são compactadas
- Apenas a mais recente é mantida

**3. Consumer reconstrói estado:**
```python
# consumer_service.py - Startup
estado = {}
for msg in consumer.poll_from_beginning():
    estado[msg.key] = msg.value  # Dict em memória
# estado = {"1": {"saldo": 150.0}, "2": {"saldo": 50.0}}
```

**4. API responde consultas:**
```python
# consumer_service.py - API
@app.route("/saldo/<cliente_id>")
def get_saldo(cliente_id):
    return estado.get(cliente_id)  # Busca em memória
```

---

## 💻 Tecnologias Usadas

### Core
- **Python 3.8+** - Linguagem principal
- **Apache Kafka** - Message broker com tópico compactado
- **Zookeeper** - Coordenação do cluster Kafka

### Python Libraries
- **Flask** - Web framework para API REST
- **Flask-CORS** - Habilitar CORS na API
- **confluent-kafka** - Cliente Kafka para Python
- **python-dotenv** - Gerenciar variáveis de ambiente

### DevOps
- **Docker Compose** - Orquestração Kafka + Zookeeper
- **Shell Scripts** - Automação (start.sh, stop.sh)

### Frontend (Bonus)
- **HTML/CSS/JavaScript** - Dashboard web interativo

---

## 📦 Instalação

### Pré-requisitos

- **Docker** e **Docker Compose** instalados
- **Python 3.8+** instalado
- **Git** (para clonar o repositório)

### Passo 1: Clonar o Repositório

```bash
git clone https://github.com/MathiasWhite1023/kafka-saldo-demo.git
cd kafka-saldo-demo
git checkout python-version
```

### Passo 2: Iniciar Kafka

```bash
# Inicia Kafka e Zookeeper em containers Docker
docker-compose up -d

# Aguarda Kafka estar pronto (5-10 segundos)
sleep 10

# Cria o tópico compactado
./create_topic.sh
```

**O que acontece:**
- Kafka sobe na porta `9092`
- Zookeeper sobe na porta `2181`
- Tópico `contas` é criado com `cleanup.policy=compact`

### Passo 3: Instalar Dependências Python

```bash
# Criar ambiente virtual
python3 -m venv .venv

# Ativar ambiente virtual
source .venv/bin/activate  # Linux/Mac
# .venv\Scripts\activate   # Windows

# Instalar dependências
pip install -r requirements.txt
```

**Dependências instaladas:**
```
Flask==2.3.2
Flask-CORS==4.0.0
confluent-kafka==2.1.1
python-dotenv==1.0.0
```

---

## 🚀 Como Usar

### Opção 1: Script Automatizado (Recomendado)

```bash
./start.sh
```

Este script faz tudo automaticamente:
1. ✅ Inicia Kafka e Zookeeper
2. ✅ Cria o tópico compactado
3. ✅ Cria virtualenv e instala dependências
4. ✅ Envia mensagens de teste
5. ✅ Inicia a API REST

### Opção 2: Passo a Passo Manual

#### 1. Enviar Mensagens de Teste

```bash
# Ativar virtualenv
source .venv/bin/activate

# Enviar mensagens via producer batch
python producer.py
```

**Saída esperada:**
```
Produzido -> key=1 value={'cliente_id': '1', 'saldo': 200.0, ...}
Produzido -> key=2 value={'cliente_id': '2', 'saldo': 50.0, ...}
Produzido -> key=1 value={'cliente_id': '1', 'saldo': 150.0, ...}
Mensagens enviadas.
```

#### 2. Iniciar Consumer Service (API)

```bash
python consumer_service.py
```

**Saída esperada:**
```
Reconstruindo estado a partir do tópico...
Reconstrução finalizada. Número de chaves: 2
Consumer streaming iniciado, aguardando novas mensagens...
 * Running on http://127.0.0.1:5001
```

#### 3. Adicionar Novos Clientes (Opcional)

```bash
# Em outro terminal
source .venv/bin/activate
python producer_interactive.py
```

**Uso:**
```
Cliente ID: 3
Saldo: 500.00
✅ Mensagem enviada!

Cliente ID: 4
Saldo: 1000.00
✅ Mensagem enviada!
```

---

## 🧪 Testando

### 1. Testar API via cURL

**Consultar saldo de um cliente:**
```bash
curl http://localhost:5001/saldo/1
```

**Resposta:**
```json
{
  "cliente_id": "1",
  "saldo": 150.0,
  "timestamp": "2025-10-14T15:20:57"
}
```

**Listar todos os saldos:**
```bash
curl http://localhost:5001/saldos
```

**Resposta:**
```json
{
  "total": 2,
  "clientes": [
    {"cliente_id": "1", "saldo": 150.0, "timestamp": "2025-10-14T15:20:57"},
    {"cliente_id": "2", "saldo": 50.0, "timestamp": "2025-10-14T15:20:56"}
  ]
}
```

### 2. Dashboard Web

**Iniciar servidor HTTP:**
```bash
python3 -m http.server 8080
```

**Acessar dashboard:**
- Simples: http://localhost:8080/dashboard.html
- Universal: http://localhost:8080/dashboard-universal.html

**Features do Dashboard:**
- 🔍 Busca de saldo por cliente ID
- 📊 Visualização bonita dos dados
- ⚡ Status da API em tempo real
- 🎨 Interface moderna e responsiva

### 3. Demonstrar Compactação do Kafka

**Enviar múltiplas atualizações para o mesmo cliente:**
```bash
python producer_interactive.py

# Digite:
Cliente ID: 5
Saldo: 100.00

Cliente ID: 5
Saldo: 200.00

Cliente ID: 5
Saldo: 300.00
```

**Consultar saldo:**
```bash
curl http://localhost:5001/saldo/5
```

**Resultado:**
```json
{
  "cliente_id": "5",
  "saldo": 300.0,  # ← Apenas o último valor!
  "timestamp": "..."
}
```

**✨ Isso prova que o Kafka está compactando:** apenas a última mensagem por chave é mantida!

### 4. Testar Reconstrução de Estado

**Parar o consumer:**
```bash
pkill -f consumer_service.py
```

**Reiniciar:**
```bash
python consumer_service.py
```

**Observar logs:**
```
Reconstruindo estado a partir do tópico...
Reconstrução finalizada. Número de chaves: 5  # ← Estado reconstruído!
```

**Consultar novamente:**
```bash
curl http://localhost:5001/saldo/5
# ✅ Dados ainda estão lá! Estado foi reconstruído do Kafka
```

---

## 🔍 Entendendo o Código

### 1. Producer (`producer_interactive.py`)

```python
from confluent_kafka import Producer
import json, os
from datetime import datetime

# Configuração do producer
producer = Producer({'bootstrap.servers': 'localhost:9092'})

# Função para enviar mensagem
def send_update(cliente_id, saldo):
    value = {
        "cliente_id": cliente_id,
        "saldo": saldo,
        "timestamp": datetime.now().isoformat()
    }
    
    # KEY = cliente_id (importante para compactação!)
    producer.produce(
        topic='contas',
        key=str(cliente_id),           # Chave = ID do cliente
        value=json.dumps(value)        # Valor = JSON com saldo
    )
    producer.flush()  # Garante envio
```

**🔑 Ponto-chave:** A `key` é o cliente_id! Isso permite ao Kafka compactar mensagens do mesmo cliente.

### 2. Consumer Service (`consumer_service.py`)

**Reconstrução de Estado (Startup):**
```python
def rebuild_state():
    consumer = Consumer({
        'bootstrap.servers': 'localhost:9092',
        'group.id': 'consulta-saldo-rebuild',
        'auto.offset.reset': 'earliest'  # Lê do início!
    })
    
    consumer.assign([TopicPartition('contas', 0, 0)])  # Partição 0, offset 0
    
    estado = {}
    while True:
        msg = consumer.poll(1.0)
        if msg is None:
            break  # Fim do tópico
        
        key = msg.key().decode('utf-8')
        value = json.loads(msg.value().decode('utf-8'))
        estado[key] = value  # Última mensagem por key
    
    return estado  # Dict em memória
```

**Streaming Consumer (Runtime):**
```python
def run_streaming_consumer():
    consumer = Consumer({
        'bootstrap.servers': 'localhost:9092',
        'group.id': 'consulta-saldo',
        'auto.offset.reset': 'latest'  # Só novas mensagens
    })
    
    consumer.subscribe(['contas'])
    
    while True:
        msg = consumer.poll(1.0)
        if msg is None:
            continue
        
        key = msg.key().decode('utf-8')
        value = json.loads(msg.value().decode('utf-8'))
        
        with estado_lock:
            estado[key] = value  # Atualiza estado em memória
        
        print(f"Atualizado: {key} -> {value}")
```

**API REST (Flask):**
```python
from flask import Flask, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # Permite chamadas do dashboard web

@app.route("/saldo/<cliente_id>")
def get_saldo(cliente_id):
    with estado_lock:
        data = estado.get(str(cliente_id))
    
    if data:
        return jsonify(data)
    else:
        return jsonify({"error": "cliente não encontrado"}), 404

@app.route("/saldos")
def get_all():
    with estado_lock:
        all_data = [{"cliente_id": k, **v} for k, v in estado.items()]
    return jsonify({"total": len(all_data), "clientes": all_data})
```

### 3. Docker Compose (`docker-compose.yml`)

```yaml
version: '3'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_LOG_CLEANUP_POLICY: compact  # ← Compactação!
```

### 4. Criação do Tópico (`create_topic.sh`)

```bash
docker exec kafka-saldo-demo-kafka-1 \
  kafka-topics --create \
    --bootstrap-server localhost:9092 \
    --topic contas \
    --partitions 1 \
    --replication-factor 1 \
    --config cleanup.policy=compact \      # ← COMPACTAÇÃO!
    --config segment.ms=10000 \            # Compacta a cada 10s
    --config min.cleanable.dirty.ratio=0.01
```

**Configurações importantes:**
- `cleanup.policy=compact` - Ativa compactação
- `segment.ms=10000` - Força compactação frequente (10s)
- `min.cleanable.dirty.ratio=0.01` - Compacta agressivamente

---

## 📊 Estrutura do Projeto

```
kafka-saldo-demo/ (branch python-version)
│
├── 📄 README.md                    # Esta documentação
├── 📄 TESTING.md                   # Cenários de teste detalhados
├── 📄 GUIA_COMPLETO.md            # Guia completo do sistema
├── 📄 QUICK_START.md              # Início rápido
├── 📄 STATUS.txt                  # Status do sistema
│
├── 🐍 CÓDIGO PYTHON
│   ├── consumer_service.py        # Consumer + API REST
│   ├── producer.py                # Producer batch (demo)
│   └── producer_interactive.py    # Producer interativo (CLI)
│
├── 🐳 INFRAESTRUTURA
│   ├── docker-compose.yml         # Kafka + Zookeeper
│   ├── create_topic.sh            # Cria tópico compactado
│   ├── start.sh                   # Inicia tudo
│   └── stop.sh                    # Para tudo
│
├── 📦 DEPENDÊNCIAS
│   ├── requirements.txt           # Libs Python
│   └── .gitignore                 # Arquivos ignorados
│
└── 🌐 FRONTEND
    ├── dashboard.html             # Dashboard simples
    └── dashboard-universal.html   # Dashboard avançado
```

---

## 🛑 Parar o Sistema

```bash
./stop.sh
```

Ou manualmente:
```bash
# Parar consumer
pkill -f consumer_service.py

# Parar Kafka
docker-compose down
```

---

## 🐛 Troubleshooting

### Erro: "Connection refused" ao conectar no Kafka

**Problema:** Kafka ainda não está pronto.

**Solução:**
```bash
# Aguardar Kafka inicializar
sleep 10

# Verificar se está rodando
docker ps | grep kafka
```

### Erro: "Port 5001 already in use"

**Problema:** Porta 5001 em uso (AirPlay no macOS).

**Solução 1 - Desabilitar AirPlay:**
- System Settings → General → AirDrop & Handoff
- Desligar "AirPlay Receiver"

**Solução 2 - Mudar porta no código:**
```python
# consumer_service.py (última linha)
app.run(port=5002)  # Usar porta 5002
```

### Erro: "No messages in topic"

**Problema:** Tópico vazio.

**Solução:**
```bash
# Enviar mensagens de teste
python producer.py
```

### Consumer não reconstrói estado

**Problema:** Consumer não está lendo do início.

**Solução:**
```python
# Verificar auto.offset.reset em consumer_service.py
'auto.offset.reset': 'earliest'  # ← Deve ser 'earliest'
```

---

## 📚 Recursos Adicionais

### Documentação

- [Apache Kafka Docs](https://kafka.apache.org/documentation/)
- [Confluent Kafka Python](https://docs.confluent.io/kafka-clients/python/current/overview.html)
- [Log Compaction](https://kafka.apache.org/documentation/#compaction)
- [Flask Quickstart](https://flask.palletsprojects.com/quickstart/)

### Conceitos Avançados

- **Event Sourcing** - Estado derivado de eventos
- **CQRS** - Separação de leitura/escrita
- **Stateful Services** - Serviços com estado derivado de eventos
- **Log Compaction** - Manter apenas último estado por chave

---

## 🎯 Casos de Uso Reais

Este padrão é usado em:

1. **Banking/Fintech** - Saldos de contas, transações
2. **E-commerce** - Inventário de produtos, carrinhos
3. **Gaming** - Estado de jogadores, rankings
4. **IoT** - Último estado de dispositivos
5. **Monitoring** - Métricas e status de sistemas

---

## 🤝 Contribuindo

Contribuições são bem-vindas!

1. Fork o projeto
2. Crie uma branch: `git checkout -b feature/MinhaFeature`
3. Commit: `git commit -m 'Adiciona MinhaFeature'`
4. Push: `git push origin feature/MinhaFeature`
5. Abra um Pull Request

---

## 📝 Licença

Este projeto é open source sob a licença MIT.

---

## 🌟 Outras Versões

- ☕ **Versão Java/Spring Boot:** [java-version branch](../../tree/java-version)
- 📖 **README Principal:** [main branch](../../tree/main)

---

**Desenvolvido com 🐍 e ❤️ - Demonstrando o poder do Apache Kafka!**
