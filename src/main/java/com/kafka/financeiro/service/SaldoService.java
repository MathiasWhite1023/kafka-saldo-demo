package com.kafka.financeiro.service;

import com.kafka.financeiro.config.KafkaTopics;
import com.kafka.financeiro.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Serviço que mantém estado dos saldos das contas
 * Consome do tópico 'saldos' (compactado)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaldoService {

    // Estado em memória dos saldos
    private final Map<String, Saldo> saldos = new ConcurrentHashMap<>();

    /**
     * Reconstrói estado inicial lendo todo o tópico
     */
    @PostConstruct
    public void init() {
        log.info("Inicializando SaldoService - aguardando reconstrução de estado...");
    }

    /**
     * Consome mensagens do tópico 'saldos'
     * Tópico compactado - mantém apenas último saldo de cada conta
     */
    @KafkaListener(
        topics = KafkaTopics.SALDOS,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeSaldo(Saldo saldo) {
        log.info("Saldo recebido: conta={}, saldo={}", 
            saldo.getContaId(), saldo.getSaldoAtual());
        
        saldos.put(saldo.getContaId(), saldo);
    }

    /**
     * Consulta saldo de uma conta
     */
    public Saldo getSaldo(String contaId) {
        return saldos.get(contaId);
    }

    /**
     * Consulta todos os saldos
     */
    public List<Saldo> getAllSaldos() {
        return saldos.values().stream()
            .sorted((a, b) -> a.getContaId().compareTo(b.getContaId()))
            .collect(Collectors.toList());
    }

    /**
     * Consulta saldos com filtro
     */
    public List<Saldo> getSaldosComFiltro(BigDecimal saldoMinimo) {
        return saldos.values().stream()
            .filter(s -> s.getSaldoAtual().compareTo(saldoMinimo) >= 0)
            .sorted((a, b) -> b.getSaldoAtual().compareTo(a.getSaldoAtual()))
            .collect(Collectors.toList());
    }

    /**
     * Estatísticas dos saldos
     */
    public Map<String, Object> getEstatisticas() {
        BigDecimal totalSaldos = saldos.values().stream()
            .map(Saldo::getSaldoAtual)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal mediaSaldos = saldos.isEmpty() 
            ? BigDecimal.ZERO 
            : totalSaldos.divide(BigDecimal.valueOf(saldos.size()), 2, BigDecimal.ROUND_HALF_UP);

        return Map.of(
            "totalContas", saldos.size(),
            "saldoTotal", totalSaldos,
            "saldoMedio", mediaSaldos
        );
    }
}
