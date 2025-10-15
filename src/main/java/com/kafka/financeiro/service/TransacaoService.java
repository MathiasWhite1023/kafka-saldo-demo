package com.kafka.financeiro.service;

import com.kafka.financeiro.config.KafkaTopics;
import com.kafka.financeiro.model.Transacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Serviço que processa transações
 * Consome do tópico 'transacoes'
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransacaoService {

    // Histórico de transações em memória
    private final Map<String, Transacao> transacoes = new ConcurrentHashMap<>();

    /**
     * Consome transações do Kafka
     */
    @KafkaListener(
        topics = KafkaTopics.TRANSACOES,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTransacao(Transacao transacao) {
        log.info("Transação recebida: id={}, tipo={}, valor={}", 
            transacao.getTransacaoId(), 
            transacao.getTipo(), 
            transacao.getValor());
        
        transacoes.put(transacao.getTransacaoId(), transacao);
        
        // Aqui você poderia processar a transação:
        // - Atualizar saldo
        // - Gerar item de extrato
        // - Enviar notificação
        // - Registrar auditoria
    }

    /**
     * Busca transação por ID
     */
    public Transacao getTransacao(String transacaoId) {
        return transacoes.get(transacaoId);
    }

    /**
     * Lista todas as transações
     */
    public List<Transacao> getAllTransacoes() {
        return transacoes.values().stream()
            .sorted((a, b) -> b.getDataHora().compareTo(a.getDataHora()))
            .collect(Collectors.toList());
    }

    /**
     * Filtra transações por conta
     */
    public List<Transacao> getTransacoesPorConta(String contaId) {
        return transacoes.values().stream()
            .filter(t -> contaId.equals(t.getContaOrigem()) || contaId.equals(t.getContaDestino()))
            .sorted((a, b) -> b.getDataHora().compareTo(a.getDataHora()))
            .collect(Collectors.toList());
    }

    /**
     * Filtra transações por status
     */
    public List<Transacao> getTransacoesPorStatus(Transacao.StatusTransacao status) {
        return transacoes.values().stream()
            .filter(t -> t.getStatus() == status)
            .sorted((a, b) -> b.getDataHora().compareTo(a.getDataHora()))
            .collect(Collectors.toList());
    }

    /**
     * Estatísticas de transações
     */
    public Map<String, Object> getEstatisticas() {
        long total = transacoes.size();
        
        Map<Transacao.StatusTransacao, Long> porStatus = transacoes.values().stream()
            .collect(Collectors.groupingBy(Transacao::getStatus, Collectors.counting()));

        Map<Transacao.TipoTransacao, Long> porTipo = transacoes.values().stream()
            .collect(Collectors.groupingBy(Transacao::getTipo, Collectors.counting()));

        return Map.of(
            "total", total,
            "porStatus", porStatus,
            "porTipo", porTipo
        );
    }
}
