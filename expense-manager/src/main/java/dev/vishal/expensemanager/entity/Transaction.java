package dev.vishal.expensemanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "Transaction")
public class Transaction {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "logical_transaction_id", columnDefinition = "uuid")
    private UUID logicalTransactionId;

    @Column(name = "version_number", nullable = false)
    private Long versionNumber;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "note")
    private String note;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "transaction_datetime", nullable = false)
    private LocalDateTime transactionDatetime;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Transient
    private transient String accountName;

    @Transient
    private transient String categoryName;

    @PrePersist
    protected void onCreate() {
        createdOn = LocalDateTime.now();
        updatedOn = LocalDateTime.now();
        isDeleted = false; // default value
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = LocalDateTime.now();
    }
}
