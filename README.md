# 🚀 Kafka Saldo Demo - Demonstração de Tópico Compactado

> **Projeto educacional demonstrando Apache Kafka com tópicos compactados (log compaction)**  
> Implementado em **Python** e **Java** para diferentes casos de uso

<div align="center">

[![Python](https://img.shields.io/badge/Python-3.13-blue?logo=python&logoColor=white)](https://github.com/MathiasWhite1023/kafka-saldo-demo/tree/python-version)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)](https://github.com/MathiasWhite1023/kafka-saldo-demo/tree/java-version)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.x-black?logo=apache-kafka&logoColor=white)](https://kafka.apache.org/)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

</div>

---

## � Sobre o Projeto

Este repositório demonstra de forma **prática e didática** como o **Apache Kafka** resolve problemas reais usando **tópicos compactados** (_log compaction_). O conceito é simples, mas poderoso:

### 🎯 O Problema
Você precisa manter o **estado atual** de milhares de contas bancárias, mas:
- ❌ Não quer usar banco de dados tradicional
- ❌ Precisa de histórico de mudanças (auditoria)
- ❌ Deve ser resiliente a falhas
- ❌ Precisa escalar horizontalmente

### ✅ A Solução: Kafka com Log Compaction

```
┌─────────────────────────────────────────────────────────────┐
│  TÓPICO KAFKA: "contas" (compactado)                        │
├─────────────────────────────────────────────────────────────┤
│  Key: cliente_001  →  {"saldo": 100.00, "timestamp": ...}  │
│  Key: cliente_002  →  {"saldo": 250.00, "timestamp": ...}  │
│  Key: cliente_001  →  {"saldo": 150.00, "timestamp": ...}  │  ← Sobrescreve anterior
│  Key: cliente_003  →  {"saldo": 500.00, "timestamp": ...}  │
│                                                             │
│  Após compactação, Kafka mantém apenas:                    │
│  ✅ cliente_001 → 150.00  (última atualização)             │
│  ✅ cliente_002 → 250.00                                    │
│  ✅ cliente_003 → 500.00                                    │
└─────────────────────────────────────────────────────────────┘
```

### 💡 Por Que Isso é Útil?

1. **Event Sourcing Simplificado**: Eventos são a fonte de verdade
2. **Reconstrução de Estado**: Reinicie o consumer e o estado é recuperado do Kafka
3. **Sem Banco de Dados**: O Kafka É seu banco (para este caso de uso)
4. **Auditoria**: Histórico completo de mudanças (antes da compactação)
5. **Escalabilidade**: Múltiplos consumers podem processar independentemente


---

## � Estrutura do Repositório

Este repositório está organizado em **3 branches**, cada uma com propósito específico:

### 📌 Branch: `main` (você está aqui)
- README geral do projeto
- Comparação entre versões Python e Java
- Links para cada implementação
- Overview de conceitos Kafka

### 🐍 Branch: [`python-version`](https://github.com/MathiasWhite1023/kafka-saldo-demo/tree/python-version)
**Implementação Python com Flask**

```
Ideal para:
✅ Prototipagem rápida
✅ Microserviços leves
✅ APIs REST simples
✅ Aprendizado de Kafka
✅ Dashboards web
```

**Stack:**
- Python 3.13
- Flask 2.3.2 (API REST)
- confluent-kafka (cliente Kafka)
- Docker Compose
- Dashboard HTML/JavaScript

**Features:**
- ✅ API REST (`/saldo/<id>`, `/saldos`)
- ✅ Consumer em background (threading)
- ✅ Producer interativo (CLI)
- ✅ Dashboard web em tempo real
- ✅ Scripts de automação (start.sh, stop.sh)

[**📖 Ver README Python Completo →**](https://github.com/MathiasWhite1023/kafka-saldo-demo/blob/python-version/README.md)

---

### ☕ Branch: [`java-version`](https://github.com/MathiasWhite1023/kafka-saldo-demo/tree/java-version)
**Implementação Java com Spring Boot**

```
Ideal para:
✅ Aplicações empresariais
✅ Microserviços robustos
✅ Alta concorrência
✅ Produção enterprise
✅ Integração Spring ecosystem
```

**Stack:**
- Java 17
- Spring Boot 3.2.0
- Spring Kafka 3.1.0
- Maven
- Docker Compose

**Features:**
- ✅ REST API (`/saldo/{id}`, `/saldos`, `/health`)
- ✅ @KafkaListener automático
- ✅ ConcurrentHashMap thread-safe
- ✅ Spring Dependency Injection
- ✅ Configuração externalizada (application.yml)
- ✅ Producer CLI + Batch

[**📖 Ver README Java Completo →**](https://github.com/MathiasWhite1023/kafka-saldo-demo/blob/java-version/README.md)

---

## 🆚 Comparação: Python vs Java

| Aspecto | 🐍 Python | ☕ Java |
|---------|----------|---------|
| **Complexidade** | Simples | Moderada |
| **Linhas de código** | ~200 | ~400 |
| **Startup** | Rápido (~1s) | Moderado (~3s) |
| **Memória** | Leve (~50MB) | Moderado (~200MB) |
| **Concorrência** | Threading | Nativa (ConcurrentHashMap) |
| **Frameworks** | Flask (minimalista) | Spring Boot (completo) |
| **Tipo de sistema** | Dinamicamente tipado | Estaticamente tipado |
| **Manutenção** | Scripts simples | Arquitetura em camadas |
| **Deploy** | Docker + Python | Docker + JAR |
| **Uso recomendado** | Protótipos, MVPs | Produção enterprise |

---

## 📚 Conceitos Kafka Demonstrados

Ambas as versões demonstram os mesmos conceitos fundamentais:

### 1️⃣ **Tópico Compactado** (Log Compaction)
```bash
# Configuração do tópico
cleanup.policy=compact
min.compaction.lag.ms=60000  # Aguarda 60s antes de compactar
```

**Como funciona:**
- Mensagens com mesma chave (key) sobrescrevem anteriores
- Kafka mantém apenas o valor mais recente por chave
- Garante estado atual + auditoria (antes da compactação)

### 2️⃣ **Event Sourcing**
```
Eventos → Kafka → Estado Derivado
          ↓
    Fonte de Verdade
```

**Benefícios:**
- ✅ Histórico completo
- ✅ Replay de eventos
- ✅ Auditoria nativa
- ✅ Debugging facilitado

### 3️⃣ **Reconstrução de Estado**
```python
# Ao iniciar, consumer lê TODO o tópico desde o início
auto.offset.reset=earliest

# Reconstrói estado em memória
for mensagem in topico:
    estado[mensagem.key] = mensagem.value
```

### 4️⃣ **Consumer Groups**
```
┌─────────────────────────────────────┐
│  Kafka Topic: "contas"              │
│  ├── Partition 0 → Consumer 1       │
│  ├── Partition 1 → Consumer 2       │
│  └── Partition 2 → Consumer 3       │
│                                     │
│  Group: "saldo-service"             │
└─────────────────────────────────────┘
```

---

## 🚀 Quick Start

### 1. Clone o Repositório
```bash
git clone https://github.com/MathiasWhite1023/kafka-saldo-demo.git
cd kafka-saldo-demo
```

### 2. Escolha Sua Versão

#### 🐍 Python
```bash
git checkout python-version
# Siga o README da branch python-version
```

#### ☕ Java
```bash
git checkout java-version
# Siga o README da branch java-version
```

### 3. Infraestrutura (Ambas Versões)
```bash
# Subir Kafka + Zookeeper
docker-compose up -d

# Criar tópico compactado
./create_topic.sh
```

---

## 🎯 Casos de Uso Reais

### 📊 Aplicações deste Padrão

1. **Saldo de Contas** (este projeto)
   - Mantém saldo atual de milhares de contas
   - Auditoria de transações
   - Reconstrução de estado

2. **Cache Distribuído**
   - Key-Value store baseado em Kafka
   - Sem Redis/Memcached
   - Persistente e replicado

3. **Configuração de Microserviços**
   - Cada microserviço consome configurações
   - Atualização em tempo real
   - Single source of truth

4. **Estado de Sessão**
   - Sessões de usuário em Kafka
   - Failover automático
   - Compartilhado entre instâncias

5. **Inventory/Estoque**
   - Quantidade de produtos
   - Atualização em tempo real
   - Histórico de mudanças

---

## 🔬 Experimentos Práticos

Ambas as versões permitem você experimentar:

### Teste 1: Compactação em Ação
```bash
# 1. Produzir múltiplas mensagens para mesmo cliente
# Mensagem 1: cliente_001 → saldo: 100.00
# Mensagem 2: cliente_001 → saldo: 200.00
# Mensagem 3: cliente_001 → saldo: 300.00

# 2. Aguardar 60s (min.compaction.lag.ms)

# 3. Consumir desde o início
# Resultado: Apenas última mensagem (300.00)
```

### Teste 2: Reconstrução de Estado
```bash
# 1. Parar consumer
# 2. Produzir novas mensagens
# 3. Reiniciar consumer
# Resultado: Estado reconstruído com TODAS mensagens
```

### Teste 3: Múltiplos Consumers
```bash
# 1. Iniciar Consumer 1 → Group: "app1"
# 2. Iniciar Consumer 2 → Group: "app2"
# 3. Produzir mensagem
# Resultado: AMBOS recebem (grupos diferentes)
```

---

## 📖 Documentação Completa

### 🐍 Python Version
- [**README Completo**](https://github.com/MathiasWhite1023/kafka-saldo-demo/blob/python-version/README.md)
- Conceitos Kafka explicados
- Código comentado linha por linha
- Guia de instalação
- Troubleshooting
- Dashboard web

### ☕ Java Version
- [**README Completo**](https://github.com/MathiasWhite1023/kafka-saldo-demo/blob/java-version/README.md)
- Arquitetura Spring Boot
- Spring Kafka configuração
- Código comentado
- Deploy em produção
- Métricas e health checks

---

## 🛠️ Arquitetura Geral

```
┌────────────────────────────────────────────────────────────────┐
│                      KAFKA CLUSTER                             │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Topic: "contas" (compacted)                             │  │
│  │  ├── Partition 0 → [msg1, msg2, msg3...]                 │  │
│  │  ├── Partition 1 → [msg4, msg5, msg6...]                 │  │
│  │  └── Partition 2 → [msg7, msg8, msg9...]                 │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────┘
                       ↑                    ↓
                   Produz               Consome
                       │                    │
         ┌─────────────┴────────┬──────────┴──────────┐
         │                      │                      │
    ┌────▼─────┐          ┌────▼─────┐          ┌────▼─────┐
    │ Producer │          │Consumer 1│          │Consumer 2│
    │   CLI    │          │  (API)   │          │ (Worker) │
    └──────────┘          └──────────┘          └──────────┘
                               │
                          ┌────▼─────┐
                          │ In-Memory│
                          │  State   │
                          │  (Map)   │
                          └──────────┘
                               │
                          ┌────▼─────┐
                          │REST API  │
                          │Endpoints │
                          └──────────┘
```

---

## 🏆 Aprendizados Principais

### 1. Kafka Não é Só Messaging
- ✅ É um **log distribuído**
- ✅ É uma **fonte de verdade**
- ✅ É um **storage durável**

### 2. Log Compaction é Poderoso
- ✅ Mantém estado atual
- ✅ Garante auditoria
- ✅ Elimina necessidade de banco (em alguns casos)

### 3. Event Sourcing na Prática
- ✅ Eventos → Estado
- ✅ Replay de eventos
- ✅ Debugging facilitado

### 4. Escolha da Tecnologia Importa
- **Python**: Prototipagem rápida, MVPs, simplicidade
- **Java**: Produção enterprise, concorrência, robustez

---

## 🔧 Tecnologias Utilizadas

### Infraestrutura
- **Apache Kafka** - Message broker / Event streaming
- **Zookeeper** - Coordenação do cluster Kafka
- **Docker Compose** - Orquestração de containers

### Python Version
- **Python 3.13**
- **Flask 2.3.2** - Framework web
- **confluent-kafka** - Cliente Kafka
- **python-dotenv** - Variáveis de ambiente

### Java Version
- **Java 17**
- **Spring Boot 3.2.0** - Framework aplicação
- **Spring Kafka 3.1.0** - Integração Kafka
- **Lombok** - Redução boilerplate
- **Maven** - Build tool

---

## 📈 Próximos Passos

### Para Aprendizado
1. ✅ Clone o repo e teste ambas versões
2. ✅ Experimente os testes práticos
3. ✅ Modifique o código e veja o que acontece
4. ✅ Leia os READMEs completos de cada branch

### Para Produção
1. Configure múltiplas partições
2. Adicione métricas (Prometheus)
3. Implemente health checks robustos
4. Configure replicação do Kafka
5. Adicione testes automatizados
6. Configure CI/CD

---

## 📝 Licença

Este projeto é licenciado sob a MIT License - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 👨‍💻 Autor

**Matheus White**
- GitHub: [@MathiasWhite1023](https://github.com/MathiasWhite1023)

---

## 🙏 Agradecimentos

- **Apache Kafka** pela ferramenta incrível
- **Confluent** pela documentação excelente
- **Spring Team** pelo Spring Kafka
- Comunidade Open Source

---

## 🔗 Links Úteis

### Documentação Oficial
- [Apache Kafka](https://kafka.apache.org/documentation/)
- [Kafka Log Compaction](https://kafka.apache.org/documentation/#compaction)
- [Confluent Kafka](https://docs.confluent.io/)
- [Spring Kafka](https://spring.io/projects/spring-kafka)

### Tutoriais
- [Kafka in 5 Minutes](https://kafka.apache.org/quickstart)
- [Spring Boot + Kafka](https://spring.io/guides/gs/messaging-kafka/)
- [confluent-kafka Python](https://docs.confluent.io/kafka-clients/python/current/overview.html)

---

<div align="center">

### 🌟 Se este projeto te ajudou, deixe uma ⭐!

**[Ver Versão Python 🐍](https://github.com/MathiasWhite1023/kafka-saldo-demo/tree/python-version)** | 
**[Ver Versão Java ☕](https://github.com/MathiasWhite1023/kafka-saldo-demo/tree/java-version)**

</div>


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
