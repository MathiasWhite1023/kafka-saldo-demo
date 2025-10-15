package com.kafka.financeiro.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo para saldos de contas
 * Tópico Kafka: saldos (compactado)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Saldo {
    
    private String contaId;
    private BigDecimal saldoAtual;
    private BigDecimal saldoBloqueado;
    private BigDecimal limite;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime ultimaAtualizacao;
    
    private String moeda;
    
    public BigDecimal getSaldoDisponivel() {
        return saldoAtual.subtract(saldoBloqueado);
    }
    
    public BigDecimal getSaldoTotal() {
        return saldoAtual.add(limite);
    }
}
