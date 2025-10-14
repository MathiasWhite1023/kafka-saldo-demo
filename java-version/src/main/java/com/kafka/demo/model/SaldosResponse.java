package com.kafka.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaldosResponse {
    private int total;
    private List<SaldoInfo> clientes;
}
