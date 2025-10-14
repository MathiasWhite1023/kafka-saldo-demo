package com.kafka.demo.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.demo.model.SaldoInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaldoProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kafka.topic.name}")
    private String topicName;

    /**
     * Envia uma atualização de saldo para o Kafka
     */
    public void sendSaldoUpdate(String clienteId, Double saldo) {
        try {
            SaldoInfo saldoInfo = new SaldoInfo(clienteId, saldo);
            String message = objectMapper.writeValueAsString(saldoInfo);
            
            kafkaTemplate.send(topicName, clienteId, message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Mensagem enviada com sucesso - Key: {}, Saldo: {}", 
                                    clienteId, saldo);
                        } else {
                            log.error("Erro ao enviar mensagem: {}", ex.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Erro ao serializar mensagem: {}", e.getMessage(), e);
        }
    }
}
