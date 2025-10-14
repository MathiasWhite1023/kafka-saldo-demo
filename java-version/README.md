# 🚀 Kafka Saldo Demo - Versão Java

Sistema de consulta de saldos usando **Apache Kafka com tópico compactado** + **Spring Boot**.

## 📋 Tecnologias

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Kafka**
- **Apache Kafka 3.6.1**
- **Maven**
- **Lombok**

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
                              │  Spring Boot    │
                              │  Consumer API   │
                              └────────┬─────────┘
                                       │
                              ┌────────▼─────────┐
                              │ GET /saldo/{id}  │
                              │ GET /saldos      │
                              │ GET /health      │
                              └──────────────────┘
```

## 📁 Estrutura do Projeto

```
java-version/
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/kafka/demo/
│       │   ├── KafkaSaldoDemoApplication.java  # Main class
│       │   ├── config/
│       │   │   └── KafkaConsumerConfig.java    # Configuração Kafka
│       │   ├── controller/
│       │   │   └── SaldoController.java        # REST endpoints
│       │   ├── service/
│       │   │   └── SaldoService.java           # Lógica de negócio
│       │   ├── model/
│       │   │   ├── SaldoInfo.java              # Modelo de dados
│       │   │   └── SaldosResponse.java         # Response DTO
│       │   ├── producer/
│       │   │   └── SaldoProducer.java          # Producer Kafka
│       │   └── cli/
│       │       └── ProducerCLI.java            # CLI interativo
│       └── resources/
│           └── application.yml                 # Configurações
└── README.md
```

## 🚦 Como Rodar

### Pré-requisitos

- Java 17+
- Maven 3.6+
- Docker (para Kafka)
- Kafka rodando na porta 9092

### 1️⃣ Iniciar Kafka

Se ainda não estiver rodando:

```bash
cd /Users/matheus/kafka-saldo-demo
docker-compose up -d
./create_topic.sh
```

### 2️⃣ Compilar o Projeto

```bash
cd java-version
mvn clean install
```

### 3️⃣ Executar a Aplicação

```bash
mvn spring-boot:run
```

A API estará disponível em: **http://localhost:8081**

### 4️⃣ Testar os Endpoints

**Consultar saldo de um cliente:**
```bash
curl http://localhost:8081/saldo/1
```

**Listar todos os saldos:**
```bash
curl http://localhost:8081/saldos
```

**Health check:**
```bash
curl http://localhost:8081/health
```

## 🧪 Enviando Mensagens de Teste

### Opção 1: Usando o producer Python

```bash
cd /Users/matheus/kafka-saldo-demo
source .venv/bin/activate
python producer_interactive.py
```

### Opção 2: Usando curl diretamente no Producer Java

Você pode criar um endpoint REST no producer ou usar a CLI interativa.

Para habilitar a **CLI interativa**, descomente a linha `@Component` em:
```java
// src/main/java/com/kafka/demo/cli/ProducerCLI.java
@Component  // <-- Descomente esta linha
public class ProducerCLI implements CommandLineRunner {
```

Depois execute:
```bash
mvn spring-boot:run
```

## 📊 Dashboard Web

O dashboard HTML criado funciona tanto com a versão Python quanto com a Java!

Apenas altere a porta no arquivo `dashboard.html` de `5001` para `8081`:

```javascript
// Linha ~156 do dashboard.html
const response = await fetch(`http://localhost:8081/saldo/${clienteId}`);
```

Depois acesse: **http://localhost:8080/dashboard.html**

## 🔧 Configurações

Edite `src/main/resources/application.yml`:

```yaml
server:
  port: 8081  # Porta da API

spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: consulta-saldo-java
      auto-offset-reset: earliest

kafka:
  topic:
    name: contas
```

## 📝 Endpoints da API

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/saldo/{clienteId}` | Consulta saldo de um cliente |
| GET | `/saldos` | Lista todos os saldos |
| GET | `/health` | Health check da aplicação |

### Exemplos de Resposta

**GET /saldo/1**
```json
{
  "cliente_id": "1",
  "saldo": 150.0,
  "timestamp": "2025-10-14T15:20:57"
}
```

**GET /saldos**
```json
{
  "total": 2,
  "clientes": [
    {
      "cliente_id": "1",
      "saldo": 150.0,
      "timestamp": "2025-10-14T15:20:57"
    },
    {
      "cliente_id": "2",
      "saldo": 50.0,
      "timestamp": "2025-10-14T15:20:56"
    }
  ]
}
```

## 🎯 Principais Features

✅ Consome mensagens de tópico Kafka compactado  
✅ Reconstrói estado em memória na inicialização  
✅ Atualiza estado em tempo real conforme novas mensagens chegam  
✅ API REST com Spring Boot  
✅ CORS habilitado  
✅ Health check endpoint  
✅ Logging estruturado  
✅ Producer integrado (opcional)  

## 🐛 Troubleshooting

**Erro de conexão com Kafka:**
- Verifique se o Kafka está rodando: `docker ps`
- Verifique o bootstrap-servers em `application.yml`

**Porta 8081 já em uso:**
- Altere a porta em `application.yml` (`server.port`)

**Nenhuma mensagem consumida:**
- Verifique se o tópico existe: `docker exec -it kafka-saldo-demo-kafka-1 kafka-topics --list --bootstrap-server localhost:9092`
- Envie mensagens usando o producer Python

## 🔄 Comparação: Python vs Java

| Aspecto | Python | Java |
|---------|--------|------|
| **Porta** | 5001 | 8081 |
| **Framework** | Flask | Spring Boot |
| **Consumer** | confluent-kafka | Spring Kafka |
| **Gerenciamento Estado** | dict + threading.Lock | ConcurrentHashMap |
| **Inicialização** | Script manual | Auto-start no Spring |
| **Hot Reload** | ❌ | ✅ (com DevTools) |
| **Type Safety** | ❌ | ✅ |
| **Produção Ready** | Precisa WSGI | ✅ Built-in |

## 📚 Próximos Passos

- [ ] Adicionar endpoint POST para atualizar saldos via API
- [ ] Implementar cache com Redis
- [ ] Adicionar métricas com Micrometer
- [ ] Implementar testes unitários e integração
- [ ] Adicionar Swagger/OpenAPI documentation
- [ ] Dockerizar aplicação Java

## 🤝 Contribuindo

Sinta-se à vontade para abrir issues e pull requests!

---

**Desenvolvido com ☕ e ❤️**
