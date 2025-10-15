package com.kafka.financeiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sistema Financeiro com Apache Kafka
 * 
 * Arquitetura baseada em Event Sourcing com múltiplos tópicos:
 * 
 * TÓPICOS COMPACTADOS (mantém apenas último estado):
 * - contas: Informações das contas bancárias
 * - saldos: Saldos atuais das contas
 * 
 * TÓPICOS DE EVENTOS (mantém histórico):
 * - transacoes: Todas as transações financeiras
 * - extratos: Itens do extrato bancário
 * - notificacoes: Notificações aos clientes
 * - auditoria: Eventos de auditoria e segurança
 * 
 * @author Sistema Financeiro
 * @version 1.0.0
 */
@SpringBootApplication
public class SistemaFinanceiroApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaFinanceiroApplication.class, args);
    }
}
