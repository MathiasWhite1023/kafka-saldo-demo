# 🏦 Sistema Financeiro com Apache Kafka

> **Arquitetura Event-Driven para transações financeiras usando múltiplos tópicos Kafka**

[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.x-black?logo=apache-kafka&logoColor=white)](https://kafka.apache.org/)

## 📖 Sobre o Projeto

Sistema financeiro demonstrando o poder do **Apache Kafka** em arquiteturas financeiras. Utiliza **7 tópicos diferentes** para eventos e dados, seguindo **Event Sourcing** e **CQRS**.

## 🗂️ Arquitetura de Tópicos

### 📦 Tópicos COMPACTADOS (mantém apenas último estado)

1. **`contas`** - Dados cadastrais das contas
2. **`saldos`** - Saldos atuais das contas

### 📝 Tópicos de EVENTOS (mantém histórico)

3. **`transacoes`** - Transações financeiras (7 dias)
4. **`extratos`** - Itens de extrato (30 dias)
5. **`notificacoes`** - Notificações (3 dias)
6. **`auditoria`** - Eventos de auditoria (1 ano)
7. **`dlq-financeiro`** - Dead Letter Queue (30 dias)

## 🚀 Quick Start

```bash
# 1. Subir Kafka
docker-compose up -d

# 2. Criar tópicos
./create_topic.sh

# 3. Compilar e executar
mvn spring-boot:run
```

## 📡 Endpoints

### Saldos
- GET `/api/saldos` - Lista todos
- GET `/api/saldos/{id}` - Consulta específico
- POST `/api/saldos` - Atualiza (publica no Kafka)

### Transações
- GET `/api/transacoes` - Lista todas
- GET `/api/transacoes/{id}` - Consulta específica
- POST `/api/transacoes` - Cria (publica no Kafka)

## 🧪 Testando

```bash
# Criar saldo
curl -X POST http://localhost:8080/api/saldos \
  -H "Content-Type: application/json" \
  -d '{"contaId":"001","saldoAtual":1000.00,"limite":500.00,"moeda":"BRL"}'

# Criar transação
curl -X POST http://localhost:8080/api/transacoes \
  -H "Content-Type: application/json" \
  -d '{"contaOrigem":"001","contaDestino":"002","valor":250.00,"tipo":"TRANSFERENCIA"}'

# Consultar
curl http://localhost:8080/api/saldos/001
```

## 🌐 Kafka UI

Acesse http://localhost:8090 para visualizar tópicos e mensagens.

## 🎯 Benefícios do Kafka

✅ Event Sourcing - Histórico completo  
✅ CQRS - Separação leitura/escrita  
✅ Alta concorrência - Sem locks  
✅ Auditoria nativa  
✅ Escalabilidade horizontal  
✅ Múltiplos consumers independentes  

## 🔧 Tecnologias

- Java 17
- Spring Boot 3.2.0
- Spring Kafka 3.1.0
- Apache Kafka 3.x
- Maven
- Docker

## 👨‍💻 Autor

**Matheus White** - [@MathiasWhite1023](https://github.com/MathiasWhite1023)

