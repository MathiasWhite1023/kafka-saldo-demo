package com.kafka.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaldoInfo {
    
    @JsonProperty("cliente_id")
    private String clienteId;
    
    @JsonProperty("saldo")
    private Double saldo;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    public SaldoInfo(String clienteId, Double saldo) {
        this.clienteId = clienteId;
        this.saldo = saldo;
        this.timestamp = LocalDateTime.now().toString();
    }
}
