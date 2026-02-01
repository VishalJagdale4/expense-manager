package dev.vishal.expensemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "dev.vishal.expensemanager.repository")
public class ExpenseManagerCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseManagerCoreApplication.class, args);
    }

}
