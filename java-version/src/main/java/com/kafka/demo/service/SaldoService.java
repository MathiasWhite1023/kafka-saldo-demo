package com.kafka.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.demo.model.SaldoInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SaldoService {

    private final Map<String, SaldoInfo> estado = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kafka.topic.name}")
    private String topicName;

    @PostConstruct
    public void init() {
        log.info("SaldoService inicializado. Aguardando mensagens do tópico: {}", topicName);
        log.info("Estado inicial: {} clientes", estado.size());
    }

    /**
     * Listener que consome mensagens do Kafka e atualiza o estado em memória
     */
    @KafkaListener(
        topics = "${kafka.topic.name}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeMessage(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        try {
            log.info("Recebida mensagem - Key: {}, Value: {}", key, message);
            
            SaldoInfo saldoInfo = objectMapper.readValue(message, SaldoInfo.class);
            estado.put(key, saldoInfo);
            
            log.info("Estado atualizado: {} -> Saldo: {}", key, saldoInfo.getSaldo());
            log.info("Total de clientes no estado: {}", estado.size());
            
        } catch (Exception e) {
            log.error("Erro ao processar mensagem: {}", e.getMessage(), e);
        }
    }

    /**
     * Consulta o saldo de um cliente específico
     */
    public Optional<SaldoInfo> getSaldo(String clienteId) {
        return Optional.ofNullable(estado.get(clienteId));
    }

    /**
     * Retorna todos os saldos em memória
     */
    public Map<String, SaldoInfo> getAllSaldos() {
        return new ConcurrentHashMap<>(estado);
    }

    /**
     * Retorna o número total de clientes
     */
    public int getTotalClientes() {
        return estado.size();
    }
}
