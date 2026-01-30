package dev.vishal.expensemanager.dao;

import dev.vishal.expensemanager.dto.TransactionDto;
import dev.vishal.expensemanager.dto.TransactionResponseDto;
import dev.vishal.expensemanager.entity.Account;
import dev.vishal.expensemanager.entity.Category;
import dev.vishal.expensemanager.entity.LogicalTransaction;
import dev.vishal.expensemanager.entity.Transaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class TransactionDaoImpl implements TransactionDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TransactionResponseDto> findTransactions(TransactionDto dto) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TransactionResponseDto> criteriaQuery = criteriaBuilder.createQuery(TransactionResponseDto.class);

        Root<Transaction> transactionRoot = criteriaQuery.from(Transaction.class);
        Root<LogicalTransaction> logicalTransactionRoot = criteriaQuery.from(LogicalTransaction.class);
        Root<Account> bankAccountRoot = criteriaQuery.from(Account.class);
        Root<Category> categoryRoot = criteriaQuery.from(Category.class);

        criteriaQuery.select(criteriaBuilder.construct(
                TransactionResponseDto.class,
                logicalTransactionRoot.get("id"),
                transactionRoot.get("amount"),
                transactionRoot.get("note"),
                transactionRoot.get("transactionType"),
                transactionRoot.get("transactionDatetime"),
                bankAccountRoot.get("id"),
                bankAccountRoot.get("name"),
                categoryRoot.get("id"),
                categoryRoot.get("name"),
                transactionRoot.get("id"),
                transactionRoot.get("createdOn"),
                transactionRoot.get("updatedOn")
        ));

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.equal(logicalTransactionRoot.get("transactionId"), transactionRoot.get("id")));
        predicates.add(criteriaBuilder.equal(transactionRoot.get("accountId"), bankAccountRoot.get("id")));
        predicates.add(criteriaBuilder.equal(transactionRoot.get("categoryId"), categoryRoot.get("id")));
        predicates.add(criteriaBuilder.equal(transactionRoot.get("isDeleted"), false));

        // filters

        // time filter
        if (Objects.nonNull(dto.getStartTime()) && Objects.nonNull(dto.getEndTime())) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(transactionRoot.get("transactionDatetime"), dto.getStartTime()));
            predicates.add(criteriaBuilder.lessThanOrEqualTo(transactionRoot.get("transactionDatetime"), dto.getEndTime()));
        }

        // bank account filter
        if (!CollectionUtils.isEmpty(dto.getAccounts())) {
            predicates.add(transactionRoot.get("accountId").in(dto.getAccounts()));
        }

        // category filter
        if (!CollectionUtils.isEmpty(dto.getCategories())) {
            predicates.add(transactionRoot.get("categoryId").in(dto.getCategories()));
        }

        // type filter
        if (Objects.nonNull(dto.getTransactionType())) {
            predicates.add(criteriaBuilder.equal(transactionRoot.get("transactionType"), (dto.getTransactionType())));
        }

        // search by note filter
        if (StringUtils.hasText(dto.getNoteLike())) {
            predicates.add(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(transactionRoot.get("note")),
                            "%" + dto.getNoteLike().toLowerCase() + "%"
                    )
            );
        }

        criteriaQuery.where(predicates);

        // sorting
        if (dto.getOrderByAsc()) {
            criteriaQuery.orderBy(criteriaBuilder.asc(transactionRoot.get("transactionDatetime")));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(transactionRoot.get("transactionDatetime")));
        }

        TypedQuery<TransactionResponseDto> query = entityManager.createQuery(criteriaQuery);

        // limit
        if (Objects.nonNull(dto.getCount())) {
            query.setMaxResults(dto.getCount().intValue());
        }

        return query.getResultList();
    }

    @Override
    public List<String> findNotes(TransactionDto transactionDto) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);

        Root<Transaction> transactionRoot = criteriaQuery.from(Transaction.class);
        Root<LogicalTransaction> logicalTransactionRoot = criteriaQuery.from(LogicalTransaction.class);

        Predicate joinCondition = criteriaBuilder.equal(logicalTransactionRoot.get("transactionId"), transactionRoot.get("id"));
        Predicate notDeleted = criteriaBuilder.equal(logicalTransactionRoot.get("isDeleted"), false);
        Predicate noteLike = criteriaBuilder.like(
                criteriaBuilder.lower(transactionRoot.get("note")),
                "%" + transactionDto.getNoteLike().toLowerCase() + "%"
        );

        // select note only
        criteriaQuery.select(transactionRoot.get("note"))
                .where(criteriaBuilder.and(joinCondition, notDeleted, noteLike))
                .groupBy(transactionRoot.get("note")) // ensures uniqueness
                .orderBy(criteriaBuilder.desc(criteriaBuilder.count(transactionRoot.get("note")))); // most frequent first

        TypedQuery<String> query = entityManager.createQuery(criteriaQuery);
        query.setMaxResults(15);

        return query.getResultList();
    }

}
