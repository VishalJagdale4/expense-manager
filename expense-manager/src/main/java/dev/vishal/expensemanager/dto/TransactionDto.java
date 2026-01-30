package dev.vishal.expensemanager.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class TransactionDto {
    private UUID id;
    private BigDecimal amount;
    private String note;
    private String transactionType;
    private Long accountId;
    private Long categoryId;
    private LocalDateTime transactionDatetime;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Boolean isDeleted;

    // For filters
    private String noteLike;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Long> accounts;
    private List<Long> categories;
    private Long count;
    private Boolean orderByAsc = false;
}