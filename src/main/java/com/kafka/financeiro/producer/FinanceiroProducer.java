package com.kafka.financeiro.producer;

import com.kafka.financeiro.config.KafkaTopics;
import com.kafka.financeiro.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Producer para enviar mensagens aos tópicos Kafka
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceiroProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publica transação
     */
    public CompletableFuture<SendResult<String, Object>> publicarTransacao(Transacao transacao) {
        log.info("Publicando transação: {}", transacao.getTransacaoId());
        return kafkaTemplate.send(KafkaTopics.TRANSACOES, transacao.getTransacaoId(), transacao);
    }

    /**
     * Publica atualização de saldo (tópico compactado)
     * A chave é o contaId para garantir compactação correta
     */
    public CompletableFuture<SendResult<String, Object>> publicarSaldo(Saldo saldo) {
        log.info("Publicando saldo: conta={}, valor={}", 
            saldo.getContaId(), saldo.getSaldoAtual());
        return kafkaTemplate.send(KafkaTopics.SALDOS, saldo.getContaId(), saldo);
    }

    /**
     * Publica atualização de conta (tópico compactado)
     */
    public CompletableFuture<SendResult<String, Object>> publicarConta(Conta conta) {
        log.info("Publicando conta: {}", conta.getContaId());
        return kafkaTemplate.send(KafkaTopics.CONTAS, conta.getContaId(), conta);
    }

    /**
     * Publica item de extrato
     */
    public CompletableFuture<SendResult<String, Object>> publicarExtrato(ItemExtrato item) {
        log.info("Publicando extrato: {}", item.getExtratoId());
        return kafkaTemplate.send(KafkaTopics.EXTRATOS, item.getExtratoId(), item);
    }

    /**
     * Publica notificação
     */
    public CompletableFuture<SendResult<String, Object>> publicarNotificacao(Notificacao notificacao) {
        log.info("Publicando notificação: tipo={}, conta={}", 
            notificacao.getTipo(), notificacao.getContaId());
        return kafkaTemplate.send(KafkaTopics.NOTIFICACOES, 
            notificacao.getNotificacaoId(), notificacao);
    }

    /**
     * Publica evento de auditoria
     */
    public CompletableFuture<SendResult<String, Object>> publicarAuditoria(EventoAuditoria evento) {
        log.info("Publicando auditoria: tipo={}, usuario={}", 
            evento.getTipo(), evento.getUsuarioId());
        return kafkaTemplate.send(KafkaTopics.AUDITORIA, evento.getEventoId(), evento);
    }
}
