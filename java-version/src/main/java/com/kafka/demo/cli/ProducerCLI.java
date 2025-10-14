package com.kafka.demo.cli;

import com.kafka.demo.producer.SaldoProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * CLI interativo para enviar atualizações de saldo
 * Descomente a anotação @Component para habilitar
 */
@Slf4j
@RequiredArgsConstructor
// @Component  // Descomente esta linha para habilitar o CLI interativo
public class ProducerCLI implements CommandLineRunner {

    private final SaldoProducer saldoProducer;

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        
        log.info("=== Producer Interativo - Kafka Saldo Demo ===");
        log.info("Digite 'sair' para encerrar\n");

        while (true) {
            try {
                System.out.print("Cliente ID: ");
                String input = scanner.nextLine().trim();
                
                if (input.equalsIgnoreCase("sair")) {
                    log.info("Encerrando producer...");
                    break;
                }
                
                String clienteId = input;
                
                System.out.print("Saldo: ");
                String saldoStr = scanner.nextLine().trim();
                Double saldo = Double.parseDouble(saldoStr);
                
                saldoProducer.sendSaldoUpdate(clienteId, saldo);
                
                System.out.println("✅ Mensagem enviada!\n");
                
            } catch (NumberFormatException e) {
                log.error("Saldo inválido! Digite um número válido.");
            } catch (Exception e) {
                log.error("Erro: {}", e.getMessage());
            }
        }
        
        scanner.close();
    }
}
