//package dev.vishal.expensemanager.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Data
//@Entity
//@Table(name = "Transaction")
//public class LogicalTransaction {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(columnDefinition = "uuid")
//    private UUID id;
//
//    @Column(name = "transaction_id")
//    private Long transactionId;
//
//    @Column(name = "current_transaction_version")
//    private Long currentTransactionVersion;
//
//    @Column(name = "account_id", nullable = false)
//    private Long accountId;
//
//    @Column(name = "category_id", nullable = false)
//    private Long categoryId;
//
//    @Column(name = "created_on")
//    private LocalDateTime createdOn;
//
//    @Column(name = "updated_on")
//    private LocalDateTime updatedOn;
//
//    @Column(name = "is_deleted")
//    private Boolean isDeleted;
//
//    @PrePersist
//    protected void onCreate() {
//        createdOn = LocalDateTime.now();
//        updatedOn = LocalDateTime.now();
//        isDeleted = false; // default value
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedOn = LocalDateTime.now();
//    }
//}
