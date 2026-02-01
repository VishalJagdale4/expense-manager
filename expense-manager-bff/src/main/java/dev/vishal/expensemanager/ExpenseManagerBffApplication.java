package dev.vishal.expensemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "dev.vishal.expensemanager.client")
public class ExpenseManagerBffApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseManagerBffApplication.class, args);
    }

}
