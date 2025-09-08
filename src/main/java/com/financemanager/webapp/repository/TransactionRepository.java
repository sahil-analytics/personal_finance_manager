package com.financemanager.webapp.repository;

import com.financemanager.webapp.model.Transaction;
import com.financemanager.webapp.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
// Optional: Import Query if you need custom JPQL or SQL queries
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> { // Entity: Transaction, PK Type: Long

    /**
     * Finds all transactions belonging to a specific user, ordered by date descending.
     *
     * @param userId The ID of the user whose transactions are to be retrieved.
     * @return A list of transactions for the user, ordered by date descending.
     */
    List<Transaction> findByUserIdOrderByDateDesc(Long userId);

    /**
     * Finds a specific transaction by its ID and the ID of its owner user.
     *
     * @param id     The ID of the transaction.
     * @param userId The ID of the user who owns the transaction.
     * @return An Optional containing the Transaction if found and owned by the user, otherwise empty.
     */
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    /**
     * Finds all transactions for a specific user within a given date range (inclusive).
     * Useful for fetching data for reports before aggregation.
     *
     * @param userId    The ID of the user.
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return A list of transactions within the specified date range for the user.
     */
    List<Transaction> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);


    /**
     * Finds all transactions for a specific user of a specific type within a given date range.
     * Used for calculating total income or total expenses for summaries.
     *
     * @param userId    The ID of the user.
     * @param type      The type of transaction (INCOME or EXPENSE).
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return A list of transactions matching the criteria.
     */
    List<Transaction> findByUserIdAndTypeAndDateBetween(Long userId, TransactionType type, LocalDate startDate, LocalDate endDate);


    // --- Optional: More efficient ways using @Query for aggregations ---
    // If performance becomes an issue with large datasets, consider JPQL queries like these:

    /*
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserIdAndTypeAndDateBetween(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT NEW com.yourcompany.personalfinancemanager.dto.CategorySpendingDTO(c.name, COALESCE(SUM(t.amount), 0)) " +
           "FROM Transaction t JOIN t.category c " +
           "WHERE t.user.id = :userId AND t.type = com.yourcompany.personalfinancemanager.model.TransactionType.EXPENSE " +
           "AND t.date BETWEEN :startDate AND :endDate " +
           "GROUP BY c.name " +
           "ORDER BY SUM(t.amount) DESC")
    List<com.yourcompany.personalfinancemanager.dto.CategorySpendingDTO> findCategorySpendingByUserIdAndDateBetween( // You'd need to create CategorySpendingDTO
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    */

}