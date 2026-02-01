package dev.vishal.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"dev.vishal.auth", "dev.common"})
public class ExpenseManagerAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseManagerAuthServiceApplication.class, args);
    }
}