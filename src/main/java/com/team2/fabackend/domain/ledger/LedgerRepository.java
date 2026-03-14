package com.team2.fabackend.domain.ledger;

import com.team2.fabackend.api.goals.dto.CategoryStatResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    /**
     * Finds all expense ledger entries for a specific user within a date range.
     *
     * @param userId The ID of the user.
     * @param start  The start date of the range.
     * @param end    The end date of the range.
     * @return A list of expense ledger entries.
     */
    @Query("SELECT l FROM Ledger l " +
            "WHERE l.userId = :userId " +
            "AND l.date BETWEEN :start AND :end " +
            "AND l.type = 'EXPENSE'")
    List<Ledger> findAllExpensesByUserIdBetween(@Param("userId") Long userId,
                                                @Param("start") LocalDate start,
                                                @Param("end") LocalDate end);

    /**
     * Calculates the total expense amount for a specific user within a date range.
     *
     * @param userId The ID of the user.
     * @param start  The start date of the range.
     * @param end    The end date of the range.
     * @return The sum of expense amounts.
     */
    @Query("SELECT SUM(l.amount) FROM Ledger l " +
            "WHERE l.userId = :userId " +
            "AND l.date BETWEEN :start AND :end " +
            "AND l.type = 'EXPENSE'")
    Long sumExpenseAmountBetween(@Param("userId") Long userId,
                                 @Param("start") LocalDate start,
                                 @Param("end") LocalDate end);

    /**
     * Calculates the average expense amount for users within a certain age range during a specific month.
     *
     * @param startDate  The start of the birth date range for the age group.
     * @param endDate    The end of the birth date range for the age group.
     * @param monthStart The start of the month.
     * @param monthEnd   The end of the month.
     * @return The average expense amount.
     */
    @Query("SELECT AVG(l.amount) FROM Ledger l JOIN User u ON l.userId = u.id " +
            "WHERE u.birth BETWEEN :startDate AND :endDate " +
            "AND l.type = 'EXPENSE' " +
            "AND l.date BETWEEN :monthStart AND :monthEnd")
    Double findAverageExpenseByAgeRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd
    );

    /**
     * Retrieves expense statistics grouped by category within a date range.
     *
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return A list of CategoryStatResponse objects.
     */
    @Query("SELECT new com.team2.fabackend.api.goals.dto.CategoryStatResponse(l.category, SUM(l.amount)) " +
            "FROM Ledger l " +
            "WHERE l.date BETWEEN :startDate AND :endDate " +
            "AND l.type = 'EXPENSE' " +
            "GROUP BY l.category")
    List<CategoryStatResponse> findCategoryStatsBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Finds all ledger entries for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of all ledger entries for the user.
     */
    List<Ledger> findAllByUserId(Long userId);

    /**
     * Finds all ledger entries within a specific date range.
     *
     * @param start The start date.
     * @param end   The end date.
     * @return A list of ledger entries within the range.
     */
    List<Ledger> findByDateBetween(LocalDate start, LocalDate end);

    /**
     * Calculates the sum of expenses per category for a user within a date range.
     *
     * @param userId    The ID of the user.
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return A list of MonthlyCategorySumLedger objects.
     */
    @Query("SELECT new com.team2.fabackend.domain.ledger.MonthlyCategorySumLedger(" +
            "l.category, SUM(l.amount)) " +
            "FROM Ledger l " +
            "WHERE l.userId = :userId " +
            "AND l.type = 'EXPENSE' " +
            "AND l.date BETWEEN :startDate AND :endDate " +
            "GROUP BY l.category")
    List<MonthlyCategorySumLedger> findMonthlyCategorySumByUserId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Retrieves detailed expense records for a user within a date range, ordered by date and time.
     *
     * @param userId    The ID of the user.
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return A list of MonthlyLedgerDetailResponse objects.
     */
    @Query("SELECT new com.team2.fabackend.domain.ledger.MonthlyLedgerDetailResponse(" +
            "l.category, l.amount, l.date, l.time) " +
            "FROM Ledger l " +
            "WHERE l.userId = :userId " +
            "AND l.type = 'EXPENSE' " +
            "AND l.date BETWEEN :startDate AND :endDate " +
            "ORDER BY l.date DESC, l.time DESC")
    List<MonthlyLedgerDetailResponse> findMonthlyLedgerDetailsByUserId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
