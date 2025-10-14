# 🚀 Kafka Saldo Demo

Sistema de consulta de saldos usando **Apache Kafka com tópico compactado** + **API REST**.

Demonstração completa de como usar Kafka como fonte de estado para aplicações, disponível em **duas implementações**: Python e Java.

---

## 📌 Sobre o Projeto

Este projeto demonstra o uso de **Apache Kafka com tópico compactado** (log compaction) como fonte de verdade para um sistema de consulta de saldos bancários.

### 🎯 Conceito Principal

O Kafka mantém apenas a **última mensagem** por chave (cliente_id) graças à compactação. Isso permite:

- ✅ Reconstruir o estado completo consumindo o tópico do início
- ✅ Manter estado atualizado em tempo real
- ✅ Alta disponibilidade e tolerância a falhas
- ✅ Escalabilidade horizontal

---

## 🌐 Duas Implementações Disponíveis

Este repositório contém **duas versões completas e funcionais**:

### 🐍 Versão Python
**Branch:** [`python-version`](../../tree/python-version)

- **Framework:** Flask
- **Consumer:** confluent-kafka
- **Porta API:** 5001
- **Características:**
  - ✅ Código simples e direto (~100 linhas)
  - ✅ Rápido para prototipar
  - ✅ Dashboard web interativo
  - ✅ Producer interativo CLI
  - ✅ Docker Compose incluído

**📖 [Ver documentação Python](../../tree/python-version/README.md)**

---

### ☕ Versão Java
**Branch:** [`java-version`](../../tree/java-version)

- **Framework:** Spring Boot 3.2.0
- **Consumer:** Spring Kafka
- **Porta API:** 8081
- **Características:**
  - ✅ Arquitetura robusta e escalável
  - ✅ Type-safe e testável
  - ✅ Health check endpoint
  - ✅ Logging estruturado
  - ✅ Producer integrado
  - ✅ CLI interativa opcional
  - ✅ Pronto para produção

**📖 [Ver documentação Java](../../tree/java-version/java-version/README.md)**

---

## 🏗️ Arquitetura

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
                              │  (Python/Java)   │
                              └────────┬─────────┘
                                       │
                              ┌────────▼─────────┐
                              │ GET /saldo/{id}  │
                              │ GET /saldos      │
                              └──────────────────┘
```

---

## � Início Rápido

### 1️⃣ Escolha uma Versão

**Para começar rápido e simples:**
```bash
git checkout python-version
```

**Para arquitetura robusta e corporativa:**
```bash
git checkout java-version
cd java-version
```

### 2️⃣ Siga a Documentação

Cada branch tem seu próprio README com instruções completas:

- 🐍 **Python:** [python-version/README.md](../../tree/python-version/README.md)
- ☕ **Java:** [java-version/README.md](../../tree/java-version/java-version/README.md)

---

## 📊 Comparação

| Aspecto | Python 🐍 | Java ☕ |
|---------|-----------|---------|
| **Framework** | Flask | Spring Boot |
| **Linhas de Código** | ~100 | ~500 |
| **Startup Time** | < 2s | ~5-8s |
| **Porta** | 5001 | 8081 |
| **Type Safety** | ❌ | ✅ |
| **Auto-reload** | ❌ | ✅ (DevTools) |
| **Produção Ready** | Precisa WSGI | ✅ Built-in |
| **Logging** | Básico | Estruturado |
| **Health Check** | ❌ | ✅ |
| **CLI Interativa** | ✅ | ✅ (Opcional) |
| **Dashboard Web** | ✅ | ✅ (Compartilhado) |

---

## 🎯 Features Comuns

Ambas as implementações oferecem:

✅ **Consumo de Kafka** - Tópico compactado  
✅ **Reconstrução de Estado** - Ao iniciar, lê todo o tópico  
✅ **Atualização em Tempo Real** - Novas mensagens atualizam estado  
✅ **API REST** - Consulta de saldos via HTTP  
✅ **CORS** - Habilitado para integração web  
✅ **Docker Compose** - Kafka + Zookeeper  
✅ **Producer** - Para enviar atualizações  
✅ **Dashboard Web** - Interface visual para consultas  
✅ **Documentação Completa** - Tutoriais e guias  

---

## 🧪 Testando

### Endpoints da API

**Python (porta 5001):**
```bash
curl http://localhost:5001/saldo/1
curl http://localhost:5001/saldos
```

**Java (porta 8081):**
```bash
curl http://localhost:8081/saldo/1
curl http://localhost:8081/saldos
curl http://localhost:8081/health
```

### Dashboard Web

Ambas as versões incluem um dashboard interativo:

```
http://localhost:8080/dashboard-universal.html
```

O dashboard permite:
- 🔍 Consultar saldo por cliente ID
- 📋 Escolher entre API Python ou Java
- 🎨 Visualização bonita dos dados
- ⚡ Status em tempo real das APIs

---

## 🎓 Casos de Uso

Este projeto demonstra padrões úteis para:

- 📊 **Event Sourcing** - Kafka como log de eventos
- 🗄️ **CQRS** - Separação de leitura/escrita
- 💾 **Stateful Microservices** - Estado reconstruído de eventos
- 🔄 **Cache Distribuído** - Estado sincronizado via Kafka
- 📈 **Real-time Analytics** - Agregações em tempo real

---

## � Documentação

### Documentos Principais

- 📄 **GUIA_COMPLETO.md** - Guia detalhado do sistema
- 📄 **QUICK_START.md** - Início rápido
- 📄 **TESTING.md** - Cenários de teste
- 📄 **STATUS.txt** - Status visual do sistema

### Por Versão

- 🐍 **Python:** [README.md](../../tree/python-version/README.md)
- ☕ **Java:** [README.md](../../tree/java-version/java-version/README.md)

---

## 🛠️ Tecnologias

### Versão Python
- Python 3.8+
- Flask
- confluent-kafka
- Docker & Docker Compose

### Versão Java
- Java 17+
- Spring Boot 3.2.0
- Spring Kafka
- Maven
- Lombok

### Infraestrutura
- Apache Kafka 3.6+
- Zookeeper
- Docker

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## � Licença

Este projeto é open source e está disponível sob a [MIT License](LICENSE).

---

## 🌟 Branches

- **`main`** - Este README e documentação geral
- **`python-version`** - Implementação completa em Python
- **`java-version`** - Implementação completa em Java

---

## 📞 Suporte

Para dúvidas ou problemas:

- 📖 Consulte a documentação em cada branch
- 🐛 Abra uma [Issue](../../issues)
- 💬 Veja a seção de troubleshooting nos READMEs

---

## 🎉 Comece Agora!

**Escolha sua versão preferida:**

```bash
# Python - Rápido e simples
git checkout python-version

# Java - Robusto e escalável
git checkout java-version
cd java-version
```

**Siga o README da branch escolhida e comece a experimentar!**

---

**Desenvolvido com ☕ e 🐍 - Demonstrando o poder do Apache Kafka! 🚀**


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
