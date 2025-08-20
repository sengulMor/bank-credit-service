package com.bank.credit.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BankCreditServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankCreditServiceApplication.class, args);
    }

}
