package com.team2.fabackend.domain.goals;

import com.team2.fabackend.api.goals.dto.CategoryStatResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    /**
     * Finds all goals associated with a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of goals for the user.
     */
    List<Goal> findAllByUserId(Long userId);

    /**
     * Retrieves expense statistics by category within a specified date range.
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
     * Calculates the total expense amount between two dates.
     *
     * @param startDate The start date of the range.
     * @param endDate   The end date of the range.
     * @return The total sum of expenses.
     */
    @Query("SELECT SUM(l.amount) FROM Ledger l " +
            "WHERE l.date BETWEEN :startDate AND :endDate " +
            "AND l.type = 'EXPENSE'")
    Long sumTotalExpenseBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
