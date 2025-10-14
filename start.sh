#!/usr/bin/env bash

# Script de Início Rápido - Kafka Saldo Demo
# ============================================

echo "🚀 Kafka Saldo Demo - Início Rápido"
echo "===================================="
echo ""

# Cores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Função para verificar se um comando existe
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Verificar pré-requisitos
echo "📋 Verificando pré-requisitos..."

if ! command_exists docker; then
    echo -e "${RED}❌ Docker não está instalado${NC}"
    exit 1
fi

if ! command_exists docker-compose; then
    echo -e "${RED}❌ Docker Compose não está instalado${NC}"
    exit 1
fi

if ! command_exists python3; then
    echo -e "${RED}❌ Python 3 não está instalado${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Todos os pré-requisitos estão instalados${NC}"
echo ""

# 1. Iniciar Kafka
echo "1️⃣  Iniciando Kafka e Zookeeper..."
docker-compose up -d

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Erro ao iniciar Kafka${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Kafka iniciado${NC}"
echo ""

# 2. Aguardar Kafka inicializar
echo "⏳ Aguardando Kafka inicializar (15 segundos)..."
sleep 15

# 3. Criar tópico
echo "2️⃣  Criando tópico compactado..."
./create_topic.sh

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Erro ao criar tópico${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Tópico criado${NC}"
echo ""

# 4. Verificar virtualenv
if [ ! -d ".venv" ]; then
    echo "3️⃣  Criando ambiente virtual Python..."
    python3 -m venv .venv
    echo -e "${GREEN}✅ Virtualenv criado${NC}"
    echo ""
fi

# 5. Instalar dependências
echo "4️⃣  Instalando dependências Python..."
source .venv/bin/activate
pip install -q -r requirements.txt

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Erro ao instalar dependências${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Dependências instaladas${NC}"
echo ""

# 6. Rodar producer
echo "5️⃣  Enviando mensagens de teste..."
python producer.py

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Erro ao enviar mensagens${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Mensagens enviadas${NC}"
echo ""

# 7. Iniciar consumer service
echo "6️⃣  Iniciando Consumer Service (API REST)..."
nohup python consumer_service.py > consumer.log 2>&1 &
CONSUMER_PID=$!

sleep 3

# Verificar se o serviço está rodando
if ps -p $CONSUMER_PID > /dev/null; then
    echo -e "${GREEN}✅ Consumer Service iniciado (PID: $CONSUMER_PID)${NC}"
else
    echo -e "${RED}❌ Erro ao iniciar Consumer Service${NC}"
    cat consumer.log
    exit 1
fi

echo ""
echo "=========================================="
echo -e "${GREEN}🎉 Sistema iniciado com sucesso!${NC}"
echo "=========================================="
echo ""
echo "📡 API disponível em: http://localhost:5000"
echo ""
echo "🧪 Testes rápidos:"
echo "  curl http://localhost:5000/saldo/1"
echo "  curl http://localhost:5000/saldo/2"
echo ""
echo "📊 Comandos úteis:"
echo "  • Ver logs do consumer: tail -f consumer.log"
echo "  • Produzir mais mensagens: python producer.py"
echo "  • Parar consumer: kill $CONSUMER_PID"
echo "  • Parar Kafka: docker-compose down"
echo ""
echo "📝 Salve o PID do consumer: $CONSUMER_PID"
echo "   (use 'kill $CONSUMER_PID' para parar o serviço)"
echo ""
