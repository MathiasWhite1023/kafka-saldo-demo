#!/bin/bash

echo "🔧 Criando tópicos Kafka para Sistema Financeiro..."
echo ""

# Aguardar Kafka estar pronto
echo "⏳ Aguardando Kafka inicializar..."
sleep 10

# TÓPICOS COMPACTADOS (mantém apenas último estado por chave)
echo "📦 Criando tópicos COMPACTADOS..."

docker exec kafka kafka-topics --create \
  --topic contas \
  --bootstrap-server localhost:9093 \
  --partitions 3 \
  --replication-factor 1 \
  --config cleanup.policy=compact \
  --config min.compaction.lag.ms=60000 \
  --config segment.ms=300000 \
  --if-not-exists

echo "✅ Tópico 'contas' criado (compactado)"

docker exec kafka kafka-topics --create \
  --topic saldos \
  --bootstrap-server localhost:9093 \
  --partitions 3 \
  --replication-factor 1 \
  --config cleanup.policy=compact \
  --config min.compaction.lag.ms=60000 \
  --config segment.ms=300000 \
  --if-not-exists

echo "✅ Tópico 'saldos' criado (compactado)"

# TÓPICOS DE EVENTOS (mantém histórico completo)
echo ""
echo "📝 Criando tópicos de EVENTOS..."

docker exec kafka kafka-topics --create \
  --topic transacoes \
  --bootstrap-server localhost:9093 \
  --partitions 3 \
  --replication-factor 1 \
  --config retention.ms=604800000 \
  --if-not-exists

echo "✅ Tópico 'transacoes' criado (retenção: 7 dias)"

docker exec kafka kafka-topics --create \
  --topic extratos \
  --bootstrap-server localhost:9093 \
  --partitions 3 \
  --replication-factor 1 \
  --config retention.ms=2592000000 \
  --if-not-exists

echo "✅ Tópico 'extratos' criado (retenção: 30 dias)"

docker exec kafka kafka-topics --create \
  --topic notificacoes \
  --bootstrap-server localhost:9093 \
  --partitions 3 \
  --replication-factor 1 \
  --config retention.ms=259200000 \
  --if-not-exists

echo "✅ Tópico 'notificacoes' criado (retenção: 3 dias)"

docker exec kafka kafka-topics --create \
  --topic auditoria \
  --bootstrap-server localhost:9093 \
  --partitions 3 \
  --replication-factor 1 \
  --config retention.ms=31536000000 \
  --if-not-exists

echo "✅ Tópico 'auditoria' criado (retenção: 1 ano)"

# DEAD LETTER QUEUE
echo ""
echo "⚠️  Criando Dead Letter Queue..."

docker exec kafka kafka-topics --create \
  --topic dlq-financeiro \
  --bootstrap-server localhost:9093 \
  --partitions 1 \
  --replication-factor 1 \
  --config retention.ms=2592000000 \
  --if-not-exists

echo "✅ Tópico 'dlq-financeiro' criado (retenção: 30 dias)"

# Listar todos os tópicos
echo ""
echo "📋 Tópicos criados:"
docker exec kafka kafka-topics --list --bootstrap-server localhost:9093

echo ""
echo "✅ Todos os tópicos foram criados com sucesso!"
echo ""
echo "🌐 Acesse Kafka UI em: http://localhost:8090"
echo ""
