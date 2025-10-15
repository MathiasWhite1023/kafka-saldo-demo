package com.kafka.financeiro.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo para transações financeiras
 * Tópico Kafka: transacoes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {
    
    private String transacaoId;
    private String contaOrigem;
    private String contaDestino;
    private BigDecimal valor;
    private TipoTransacao tipo;
    private StatusTransacao status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHora;
    
    private String descricao;
    private String categoria;
    
    public enum TipoTransacao {
        TRANSFERENCIA,
        DEPOSITO,
        SAQUE,
        PAGAMENTO,
        PIX,
        TED,
        DOC
    }
    
    public enum StatusTransacao {
        PENDENTE,
        PROCESSANDO,
        CONCLUIDA,
        FALHA,
        CANCELADA,
        ESTORNADA
    }
}
