#!/bin/bash

echo "🧪 Testando API Kafka Saldo Demo - Java"
echo ""

API_URL="http://localhost:8081"

# Cores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Função para testar endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local description=$3
    
    echo -e "${YELLOW}Testando:${NC} $description"
    echo "Endpoint: $method $API_URL$endpoint"
    
    response=$(curl -s -w "\n%{http_code}" "$API_URL$endpoint")
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✅ Sucesso (HTTP $http_code)${NC}"
        echo "$body" | python3 -m json.tool 2>/dev/null || echo "$body"
    else
        echo -e "${RED}❌ Erro (HTTP $http_code)${NC}"
        echo "$body"
    fi
    
    echo ""
}

# Health check
test_endpoint "GET" "/health" "Health Check"

# Listar todos os saldos
test_endpoint "GET" "/saldos" "Listar Todos os Saldos"

# Consultar saldo específico
test_endpoint "GET" "/saldo/1" "Consultar Saldo do Cliente 1"

test_endpoint "GET" "/saldo/2" "Consultar Saldo do Cliente 2"

# Testar cliente inexistente
test_endpoint "GET" "/saldo/999" "Consultar Cliente Inexistente (deve retornar 404)"

echo -e "${GREEN}🎉 Testes concluídos!${NC}"
