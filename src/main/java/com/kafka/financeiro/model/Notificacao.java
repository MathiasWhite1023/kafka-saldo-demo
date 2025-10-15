package com.kafka.financeiro.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Modelo para notificações
 * Tópico Kafka: notificacoes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notificacao {
    
    private String notificacaoId;
    private String contaId;
    private String transacaoId;
    private TipoNotificacao tipo;
    private String titulo;
    private String mensagem;
    private Prioridade prioridade;
    private boolean lida;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHora;
    
    private String canal; // EMAIL, SMS, PUSH, IN_APP
    
    public enum TipoNotificacao {
        TRANSACAO_REALIZADA,
        SALDO_BAIXO,
        LIMITE_ATINGIDO,
        MOVIMENTACAO_SUSPEITA,
        PAGAMENTO_AGENDADO,
        SAQUE_REALIZADO,
        DEPOSITO_RECEBIDO
    }
    
    public enum Prioridade {
        BAIXA,
        MEDIA,
        ALTA,
        URGENTE
    }
}
