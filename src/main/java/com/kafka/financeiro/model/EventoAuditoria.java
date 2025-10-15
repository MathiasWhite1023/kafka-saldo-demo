package com.kafka.financeiro.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Modelo para eventos de auditoria
 * Tópico Kafka: auditoria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoAuditoria {
    
    private String eventoId;
    private String usuarioId;
    private String contaId;
    private String transacaoId;
    private TipoEvento tipo;
    private String acao;
    private String detalhes;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHora;
    
    private String ipOrigem;
    private String dispositivo;
    private boolean sucesso;
    private String motivoFalha;
    
    public enum TipoEvento {
        LOGIN,
        LOGOUT,
        TRANSFERENCIA,
        CONSULTA_SALDO,
        ALTERACAO_DADOS,
        BLOQUEIO,
        DESBLOQUEIO,
        TENTATIVA_FRAUDE
    }
}
