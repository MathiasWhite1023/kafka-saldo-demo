package com.kafka.financeiro.controller;

import com.kafka.financeiro.model.Transacao;
import com.kafka.financeiro.producer.FinanceiroProducer;
import com.kafka.financeiro.service.TransacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller para operações com transações
 */
@RestController
@RequestMapping("/api/transacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransacaoController {

    private final TransacaoService transacaoService;
    private final FinanceiroProducer producer;

    /**
     * Consulta transação por ID
     */
    @GetMapping("/{transacaoId}")
    public ResponseEntity<Transacao> getTransacao(@PathVariable String transacaoId) {
        Transacao transacao = transacaoService.getTransacao(transacaoId);
        
        if (transacao == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(transacao);
    }

    /**
     * Lista todas as transações
     */
    @GetMapping
    public ResponseEntity<List<Transacao>> getAllTransacoes(
            @RequestParam(required = false) String contaId,
            @RequestParam(required = false) Transacao.StatusTransacao status) {
        
        List<Transacao> transacoes;
        
        if (contaId != null) {
            transacoes = transacaoService.getTransacoesPorConta(contaId);
        } else if (status != null) {
            transacoes = transacaoService.getTransacoesPorStatus(status);
        } else {
            transacoes = transacaoService.getAllTransacoes();
        }
        
        return ResponseEntity.ok(transacoes);
    }

    /**
     * Estatísticas de transações
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> getEstatisticas() {
        return ResponseEntity.ok(transacaoService.getEstatisticas());
    }

    /**
     * Cria nova transação (publica no Kafka)
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> criarTransacao(@RequestBody Transacao transacao) {
        // Gera ID se não fornecido
        if (transacao.getTransacaoId() == null) {
            transacao.setTransacaoId(UUID.randomUUID().toString());
        }
        
        transacao.setDataHora(LocalDateTime.now());
        
        // Define status inicial se não fornecido
        if (transacao.getStatus() == null) {
            transacao.setStatus(Transacao.StatusTransacao.PENDENTE);
        }
        
        producer.publicarTransacao(transacao);
        
        return ResponseEntity.ok(Map.of(
            "status", "publicado",
            "transacaoId", transacao.getTransacaoId(),
            "topico", "transacoes"
        ));
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "TransacaoService",
            "totalTransacoes", transacaoService.getAllTransacoes().size(),
            "timestamp", LocalDateTime.now()
        ));
    }
}
