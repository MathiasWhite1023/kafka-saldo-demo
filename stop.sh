#!/usr/bin/env bash

# Script de Parada - Kafka Saldo Demo
# ====================================

echo "🛑 Parando Kafka Saldo Demo..."
echo ""

# Cores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 1. Parar Consumer Service
echo "1️⃣  Parando Consumer Service..."
pkill -f "python.*consumer_service.py"
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Consumer Service parado${NC}"
else
    echo -e "${RED}⚠️  Nenhum Consumer Service em execução${NC}"
fi
echo ""

# 2. Parar Kafka
echo "2️⃣  Parando Kafka e Zookeeper..."
docker-compose down
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Kafka parado${NC}"
else
    echo -e "${RED}❌ Erro ao parar Kafka${NC}"
fi
echo ""

echo "=========================================="
echo -e "${GREEN}✅ Sistema parado com sucesso!${NC}"
echo "=========================================="
echo ""
echo "💡 Para limpar completamente (remover volumes):"
echo "   docker-compose down -v"
echo ""
