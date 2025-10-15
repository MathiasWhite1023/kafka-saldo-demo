package com.kafka.financeiro.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Modelo para contas
 * Tópico Kafka: contas (compactado)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conta {
    
    private String contaId;
    private String titularNome;
    private String titularCpf;
    private String agencia;
    private String numero;
    private TipoConta tipo;
    private StatusConta status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataCriacao;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime ultimaAtualizacao;
    
    public enum TipoConta {
        CORRENTE,
        POUPANCA,
        SALARIO,
        INVESTIMENTO
    }
    
    public enum StatusConta {
        ATIVA,
        INATIVA,
        BLOQUEADA,
        ENCERRADA
    }
}
