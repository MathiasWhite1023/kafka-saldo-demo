#!/bin/bash

echo "🚀 Iniciando Kafka Saldo Demo - Versão Java"
echo ""

# Verificar se o Kafka está rodando
echo "📡 Verificando Kafka..."
if ! docker ps | grep -q kafka-saldo-demo-kafka; then
    echo "❌ Kafka não está rodando!"
    echo "Iniciando Kafka..."
    cd ..
    docker-compose up -d
    sleep 8
    ./create_topic.sh
    cd java-version
fi

echo "✅ Kafka está rodando!"
echo ""

# Verificar se o Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não está instalado!"
    exit 1
fi

echo "📦 Compilando projeto..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Erro na compilação!"
    exit 1
fi

echo "✅ Compilação concluída!"
echo ""

echo "🚀 Iniciando aplicação..."
echo "API estará disponível em: http://localhost:8081"
echo ""
echo "Endpoints disponíveis:"
echo "  - GET  http://localhost:8081/saldo/{id}"
echo "  - GET  http://localhost:8081/saldos"
echo "  - GET  http://localhost:8081/health"
echo ""

mvn spring-boot:run
