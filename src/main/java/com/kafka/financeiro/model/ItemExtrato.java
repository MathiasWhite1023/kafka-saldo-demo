package com.kafka.financeiro.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo para extrato/histórico de transações
 * Tópico Kafka: extratos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemExtrato {
    
    private String extratoId;
    private String contaId;
    private String transacaoId;
    private TipoMovimentacao tipoMovimentacao;
    private BigDecimal valor;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoPosterior;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHora;
    
    private String descricao;
    private String contrapartida; // conta de origem ou destino
    
    public enum TipoMovimentacao {
        DEBITO,
        CREDITO
    }
}
