package dev.vishal.expensemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponseDto {
    private Long id;
    private BigDecimal amount;
    private String note;
    private String transactionType;
    private LocalDateTime transactionDatetime;
    private Long accountId;
    private String accountName;
    private Long categoryId;
    private String categoryName;
    private Long lastTransactionId;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}

