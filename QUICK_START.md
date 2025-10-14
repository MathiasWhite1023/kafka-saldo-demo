# 🎯 INÍCIO RÁPIDO - Kafka Saldo Demo

## ✅ SISTEMA PYTHON FUNCIONANDO AGORA!

### 📊 Abrir Dashboard
👉 **http://localhost:8080/dashboard-universal.html**

### 🧪 Testar API
```bash
curl http://localhost:5001/saldo/1
curl http://localhost:5001/saldos
```

### ➕ Adicionar Cliente
```bash
cd /Users/matheus/kafka-saldo-demo
source .venv/bin/activate
python producer_interactive.py
```

---

## ☕ INICIAR VERSÃO JAVA

### Opção 1: Script Automatizado
```bash
cd /Users/matheus/kafka-saldo-demo/java-version
./run.sh
```

### Opção 2: Maven Manual
```bash
cd /Users/matheus/kafka-saldo-demo/java-version
mvn spring-boot:run
```

### Testar Java API
```bash
curl http://localhost:8081/health
curl http://localhost:8081/saldo/1
curl http://localhost:8081/saldos
```

Ou usar o script de testes:
```bash
cd java-version
./test.sh
```

---

## 🛑 Parar Tudo

```bash
# Parar Python
pkill -f consumer_service.py
pkill -f "python.*http.server"

# Parar Java
pkill -f "spring-boot"

# Parar Kafka
cd /Users/matheus/kafka-saldo-demo
docker-compose down
```

---

## 📚 Documentação Completa

- **Guia Completo:** `GUIA_COMPLETO.md`
- **Python:** `README.md`
- **Java:** `java-version/README.md`
- **Testes:** `TESTING.md`

---

## 🆘 Problemas?

### Python não responde
```bash
pkill -f consumer_service.py
cd /Users/matheus/kafka-saldo-demo
source .venv/bin/activate
nohup python consumer_service.py > consumer.log 2>&1 &
```

### Kafka não conecta
```bash
docker-compose restart
sleep 5
./create_topic.sh
```

### Dashboard não abre
```bash
# Reiniciar servidor HTTP
pkill -f "python.*http.server"
cd /Users/matheus/kafka-saldo-demo
python3 -m http.server 8080 &
```

---

## 🎉 Você está pronto!

**Versão Python:** 🟢 RODANDO  
**Versão Java:** 🔵 PRONTA (execute ./run.sh)  
**Dashboard:** 🌐 DISPONÍVEL  

**Aproveite! 🚀**
