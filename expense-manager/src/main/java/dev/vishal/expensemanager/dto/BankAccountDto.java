package dev.vishal.expensemanager.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BankAccountDto {
    private Long id;
    private String type;
    private String name;
    private BigDecimal balance;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Boolean deleted;
}
