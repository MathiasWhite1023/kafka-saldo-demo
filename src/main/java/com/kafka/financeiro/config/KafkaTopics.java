package com.kafka.financeiro.config;

/**
 * Constantes dos tópicos Kafka
 */
public class KafkaTopics {
    
    // Tópicos compactados (mantém apenas último estado)
    public static final String CONTAS = "contas";
    public static final String SALDOS = "saldos";
    
    // Tópicos de eventos (mantém histórico)
    public static final String TRANSACOES = "transacoes";
    public static final String EXTRATOS = "extratos";
    public static final String NOTIFICACOES = "notificacoes";
    public static final String AUDITORIA = "auditoria";
    
    // Dead Letter Queue
    public static final String DLQ = "dlq-financeiro";
    
    private KafkaTopics() {
        // Utility class
    }
}
