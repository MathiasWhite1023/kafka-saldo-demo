package com.kafka.financeiro.controller;

import com.kafka.financeiro.model.Saldo;
import com.kafka.financeiro.producer.FinanceiroProducer;
import com.kafka.financeiro.service.SaldoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller para operações com saldos
 */
@RestController
@RequestMapping("/api/saldos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SaldoController {

    private final SaldoService saldoService;
    private final FinanceiroProducer producer;

    /**
     * Consulta saldo de uma conta
     */
    @GetMapping("/{contaId}")
    public ResponseEntity<Saldo> getSaldo(@PathVariable String contaId) {
        Saldo saldo = saldoService.getSaldo(contaId);
        
        if (saldo == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(saldo);
    }

    /**
     * Lista todos os saldos
     */
    @GetMapping
    public ResponseEntity<List<Saldo>> getAllSaldos(
            @RequestParam(required = false) BigDecimal saldoMinimo) {
        
        List<Saldo> saldos = saldoMinimo != null
            ? saldoService.getSaldosComFiltro(saldoMinimo)
            : saldoService.getAllSaldos();
        
        return ResponseEntity.ok(saldos);
    }

    /**
     * Estatísticas dos saldos
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> getEstatisticas() {
        return ResponseEntity.ok(saldoService.getEstatisticas());
    }

    /**
     * Atualiza saldo (publica no Kafka)
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> atualizarSaldo(@RequestBody Saldo saldo) {
        saldo.setUltimaAtualizacao(LocalDateTime.now());
        
        producer.publicarSaldo(saldo);
        
        return ResponseEntity.ok(Map.of(
            "status", "publicado",
            "contaId", saldo.getContaId(),
            "topico", "saldos"
        ));
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "SaldoService",
            "totalContas", saldoService.getAllSaldos().size(),
            "timestamp", LocalDateTime.now()
        ));
    }
}
