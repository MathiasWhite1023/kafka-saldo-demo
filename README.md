# 🚀 Kafka Saldo Demo - Tópico Compactado + API REST

Este projeto demonstra o uso de **Apache Kafka com tópico compactado** como fonte de estado para um sistema de consulta de saldos. Utilizamos Python com `confluent-kafka` e Flask para criar um serviço REST que reconstrói e mantém o estado em memória.

## 📋 Arquitetura

```
┌──────────┐     produz      ┌──────────────────┐
│ Producer │ ───────────────> │ Kafka (compacted)│
└──────────┘                  │  topic: contas   │
                              └────────┬─────────┘
                                       │
                                       │ consome
                                       ▼
                              ┌─────────────────┐
                              │ Consumer Service │
                              │  (Flask + API)   │
                              └────────┬─────────┘
                                       │
                              ┌────────▼─────────┐
                              │ GET /saldo/{id}  │
                              └──────────────────┘
```

## 📁 Estrutura do Projeto

```
kafka-saldo-demo/
├── docker-compose.yml      # Kafka + Zookeeper
├── create_topic.sh         # Script para criar tópico compactado
├── start.sh               # ⭐ Script para iniciar todo o sistema
├── stop.sh                # ⭐ Script para parar todo o sistema
├── .env                    # Configurações
├── requirements.txt        # Dependências Python
├── producer.py            # Produz atualizações de saldo
├── producer_interactive.py # ⭐ Producer interativo (CLI)
├── consumer_service.py    # Reconstrói estado e expõe API
├── README.md              # Esta documentação
└── .vscode/
    ├── tasks.json         # Tasks do VS Code
    └── launch.json        # Configurações de debug
```

## 🚦 Como Rodar

### 🎯 Início Rápido (Recomendado)

Use o script automatizado que faz tudo pra você:

```bash
./start.sh
```

Isso irá:
- ✅ Iniciar Kafka e Zookeeper
- ✅ Criar o tópico compactado
- ✅ Criar virtualenv (se necessário)
- ✅ Instalar dependências
- ✅ Enviar mensagens de teste
- ✅ Iniciar a API REST

Para parar tudo:

```bash
./stop.sh
```

### 📚 Passo a Passo Manual

Se preferir fazer manualmente:

### 1️⃣ Pré-requisitos

- Docker e Docker Compose instalados
- Python 3.8+ instalado
- VS Code (opcional, mas recomendado)

### 2️⃣ Iniciar Kafka

```bash
docker-compose up -d
```

Verificar containers:
```bash
docker ps
```

### 3️⃣ Criar Tópico Compactado

Tornar o script executável:
```bash
chmod +x create_topic.sh
```

Executar:
```bash
./create_topic.sh
```

**Alternativa manual:**
```bash
docker-compose exec kafka bash
kafka-topics --create --topic contas --bootstrap-server localhost:9092 \
  --partitions 1 --replication-factor 1 --config cleanup.policy=compact
exit
```

### 4️⃣ Configurar Python

Criar virtualenv e instalar dependências:

```bash
python -m venv .venv
source .venv/bin/activate  # No macOS/Linux
pip install -r requirements.txt
```

### 5️⃣ Iniciar Consumer Service (API)

```bash
python consumer_service.py
```

Isso irá:
- ✅ Reconstruir estado a partir do tópico
- ✅ Iniciar consumer em background
- ✅ Expor API em `http://localhost:5000`

### 6️⃣ Produzir Mensagens

**Opção 1: Producer Automático**

```bash
python producer.py
```

**Opção 2: Producer Interativo** ⭐

```bash
python producer_interactive.py
# Digite o ID do cliente e o saldo quando solicitado
```

### 7️⃣ Testar API

```bash
# Via curl
curl http://localhost:5000/saldo/1
curl http://localhost:5000/saldo/2

# Ou abra no navegador:
# http://localhost:5000/saldo/1
```

**Resposta esperada:**
```json
{
  "cliente_id": "1",
  "saldo": 150.0,
  "timestamp": "2025-10-14T10:30:45"
}
```

## 🎯 Endpoints da API

### GET /saldo/{cliente_id}

Retorna o saldo atual de um cliente.

**Sucesso (200):**
```json
{
  "cliente_id": "1",
  "saldo": 150.0,
  "timestamp": "2025-10-14T10:30:45"
}
```

**Não encontrado (404):**
```json
{
  "error": "cliente não encontrado"
}
```

## 🛠️ Usando VS Code

### Tasks Disponíveis

Pressione `Cmd+Shift+P` (macOS) ou `Ctrl+Shift+P` (Windows/Linux) e digite "Run Task":

- **Docker Compose Up** - Inicia Kafka
- **Create Topic** - Cria tópico compactado
- **Run Consumer Service** - Inicia API
- **Run Producer** - Envia mensagens

### Debug

Use F5 para iniciar debug do `consumer_service.py` ou `producer.py`

## ⚙️ Configuração

Arquivo `.env`:

```env
BOOTSTRAP_SERVERS=localhost:9092
TOPIC=contas
GROUP=consulta-saldo
```

## 📊 Como Funciona

### Tópico Compactado

O Kafka mantém **apenas a última mensagem** de cada chave (cliente_id). Isso permite:

- ✅ Estado compacto (não cresce indefinidamente)
- ✅ Reconstrução rápida do estado
- ✅ Histórico auditável (antes da compactação)

### Reconstrução de Estado

1. **Startup**: Consumer lê todo o tópico desde o início
2. **Constrói mapa** em memória: `cliente_id → última_mensagem`
3. **Consumer streaming** atualiza o mapa com novas mensagens
4. **API** consulta o mapa em memória

## ⚠️ Limitações e Riscos

### 🔴 Estado em Memória

- Restart do serviço = perda temporária de estado
- Reconstrução pode demorar em tópicos grandes
- Sem persistência local (considerar RocksDB/Kafka Streams)

### 🔴 Sem Transações

- Concorrência pode causar race conditions
- Múltiplos producers = possíveis inconsistências
- Não há isolamento ACID

### 🔴 Compactação Eventual

- Mensagens antigas permanecem até compactação
- Processo não é instantâneo
- Log pode crescer temporariamente

### 🔴 Consistência

- Não garante strong consistency
- Eventual consistency apenas
- Não substitui banco transacional

## 🚀 Melhorias para Produção

### 1. Persistência

```python
# Usar RocksDB ou Kafka Streams State Store
# Evita reconstrução completa após restart
```

### 2. Múltiplas Partições

```bash
kafka-topics --create --topic contas \
  --partitions 3 \  # múltiplas partições
  --replication-factor 3
```

### 3. Segurança

```python
# TLS/SSL + SASL
conf = {
    'security.protocol': 'SASL_SSL',
    'sasl.mechanism': 'PLAIN',
    'sasl.username': 'user',
    'sasl.password': 'pass'
}
```

### 4. Monitoramento

```python
# Adicionar métricas Prometheus
# Expor health checks
# Logging estruturado
```

### 5. Kafka Streams / ksqlDB

Considere usar ferramentas nativas do Kafka para materialização:

```sql
-- ksqlDB
CREATE TABLE saldos AS
  SELECT cliente_id, LATEST_BY_OFFSET(saldo) as saldo
  FROM contas_stream
  GROUP BY cliente_id;
```

## 🧪 Comandos Úteis

### Verificar Tópicos

```bash
docker-compose exec kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Descrever Tópico

```bash
docker-compose exec kafka kafka-topics --describe \
  --topic contas --bootstrap-server localhost:9092
```

### Consumir Manualmente

```bash
docker-compose exec kafka kafka-console-consumer \
  --topic contas --from-beginning \
  --bootstrap-server localhost:9092 \
  --property print.key=true
```

### Produzir Manualmente

```bash
docker-compose exec kafka kafka-console-producer \
  --topic contas --bootstrap-server localhost:9092 \
  --property "parse.key=true" \
  --property "key.separator=:"
# Digite: 3:{"cliente_id":"3","saldo":300.0,"timestamp":"2025-10-14T10:00:00"}
```

### Limpar Ambiente

```bash
docker-compose down -v  # Remove containers e volumes
```

## 📚 Recursos Adicionais

- [Confluent Kafka Python](https://docs.confluent.io/kafka-clients/python/current/overview.html)
- [Kafka Log Compaction](https://kafka.apache.org/documentation/#compaction)
- [Kafka Streams](https://kafka.apache.org/documentation/streams/)
- [ksqlDB](https://ksqldb.io/)

## 📝 Licença

Projeto de demonstração - use livremente para aprendizado.

---

**Dica Final:** Este projeto é ideal para **aprendizado e prototipagem**. Para produção, considere Kafka Streams ou ksqlDB para materialização robusta de estado! 🎯
