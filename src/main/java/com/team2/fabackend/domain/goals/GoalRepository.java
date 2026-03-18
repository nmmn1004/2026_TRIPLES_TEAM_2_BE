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
     * 특정 사용자와 연관된 모든 목표를 찾습니다.
     *
     * @param userId 사용자의 ID입니다.
     * @return 사용자의 목표 목록입니다.
     */
    List<Goal> findAllByUser_Id(Long userId);

    /**
     * 지정된 날짜 범위 내에서 카테고리별 지출 통계를 검색합니다.
     *
     * @param startDate 범위의 시작 날짜입니다.
     * @param endDate   범위의 종료 날짜입니다.
     * @return CategoryStatResponse 객체 목록입니다.
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
     * 두 날짜 사이의 총 지출 금액을 계산합니다.
     *
     * @param startDate 범위의 시작 날짜입니다.
     * @param endDate   범위의 종료 날짜입니다.
     * @return 지출의 총합입니다.
     */
    @Query("SELECT SUM(l.amount) FROM Ledger l " +
            "WHERE l.date BETWEEN :startDate AND :endDate " +
            "AND l.type = 'EXPENSE'")
    Long sumTotalExpenseBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
