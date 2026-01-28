package dev.vishal.expensemanager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "Transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "logical_transaction_id", columnDefinition = "uuid", nullable = false)
//    private UUID logicalTransactionId;
//
//    @Column(name = "version_number", nullable = false)
//    private Long versionNumber;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "note")
    private String note;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    //to be removed
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    //to be removed
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

    //to be removed
    @Column(name = "last_transaction_id")
    private Long lastTransactionId;

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
