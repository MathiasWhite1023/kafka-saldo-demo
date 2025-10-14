package com.kafka.demo.controller;

import com.kafka.demo.model.SaldoInfo;
import com.kafka.demo.model.SaldosResponse;
import com.kafka.demo.service.SaldoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SaldoController {

    private final SaldoService saldoService;

    /**
     * GET /saldo/{clienteId}
     * Consulta o saldo de um cliente específico
     */
    @GetMapping("/saldo/{clienteId}")
    public ResponseEntity<?> getSaldo(@PathVariable String clienteId) {
        log.info("Consultando saldo do cliente: {}", clienteId);
        
        return saldoService.getSaldo(clienteId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Cliente não encontrado: {}", clienteId);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * GET /saldos
     * Lista todos os saldos em memória
     */
    @GetMapping("/saldos")
    public ResponseEntity<SaldosResponse> getAllSaldos() {
        log.info("Consultando todos os saldos");
        
        Map<String, SaldoInfo> allSaldos = saldoService.getAllSaldos();
        SaldosResponse response = new SaldosResponse(
                allSaldos.size(),
                new ArrayList<>(allSaldos.values())
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /health
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "totalClientes", saldoService.getTotalClientes(),
                "timestamp", System.currentTimeMillis()
        ));
    }
}
