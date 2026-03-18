package com.team2.fabackend.domain.ledger;

import com.team2.fabackend.api.goals.dto.CategoryStatResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    /**
     * 날짜 범위 내에서 특정 사용자의 모든 지출 가계부 항목을 찾습니다.
     *
     * @param userId 사용자의 ID입니다.
     * @param start  범위의 시작 날짜입니다.
     * @param end    범위의 종료 날짜입니다.
     * @return 지출 가계부 항목 목록입니다.
     */
    @Query("SELECT l FROM Ledger l " +
            "WHERE l.user.id = :userId " +
            "AND l.date BETWEEN :start AND :end " +
            "AND l.type = 'EXPENSE'")
    List<Ledger> findAllExpensesByUserIdBetween(@Param("userId") Long userId,
                                                @Param("start") LocalDate start,
                                                @Param("end") LocalDate end);

    /**
     * 날짜 범위 내에서 특정 사용자의 총 지출 금액을 계산합니다.
     *
     * @param userId 사용자의 ID입니다.
     * @param start  범위의 시작 날짜입니다.
     * @param end    범위의 종료 날짜입니다.
     * @return 지출 금액의 합계입니다.
     */
    @Query("SELECT SUM(l.amount) FROM Ledger l " +
            "WHERE l.user.id = :userId " +
            "AND l.date BETWEEN :start AND :end " +
            "AND l.type = 'EXPENSE'")
    Long sumExpenseAmountBetween(@Param("userId") Long userId,
                                 @Param("start") LocalDate start,
                                 @Param("end") LocalDate end);

    /**
     * 특정 달 동안 특정 연령대의 사용자에 대한 평균 지출 금액을 계산합니다.
     *
     * @param startDate  연령대의 생년월일 범위 시작입니다.
     * @param endDate    연령대의 생년월일 범위 종료입니다.
     * @param monthStart 해당 월의 시작입니다.
     * @param monthEnd   해당 월의 종료입니다.
     * @return 평균 지출 금액입니다.
     */
    @Query("SELECT AVG(l.amount) FROM Ledger l JOIN User u ON l.user.id = u.id " +
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
     * 날짜 범위 내에서 카테고리별로 그룹화된 지출 통계를 검색합니다.
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
     * 특정 사용자의 모든 가계부 항목을 찾습니다.
     *
     * @param userId 사용자의 ID입니다.
     * @return 사용자의 모든 가계부 항목 목록입니다.
     */
    List<Ledger> findAllByUser_Id(Long userId);

    /**
     * 특정 날짜 범위 내의 모든 가계부 항목을 찾습니다.
     *
     * @param start 시작 날짜입니다.
     * @param end   종료 날짜입니다.
     * @return 범위 내의 가계부 항목 목록입니다.
     */
    List<Ledger> findByDateBetween(LocalDate start, LocalDate end);

    /**
     * 날짜 범위 내에서 사용자의 카테고리별 지출 합계를 계산합니다.
     *
     * @param userId    사용자의 ID입니다.
     * @param startDate 범위의 시작 날짜입니다.
     * @param endDate   범위의 종료 날짜입니다.
     * @return MonthlyCategorySumLedger 객체 목록입니다.
     */
    @Query("SELECT new com.team2.fabackend.domain.ledger.MonthlyCategorySumLedger(" +
            "l.category, SUM(l.amount)) " +
            "FROM Ledger l " +
            "WHERE l.user.id = :userId " +
            "AND l.type = 'EXPENSE' " +
            "AND l.date BETWEEN :startDate AND :endDate " +
            "GROUP BY l.category")
    List<MonthlyCategorySumLedger> findMonthlyCategorySumByUserId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 날짜 범위 내에서 사용자의 상세 지출 기록을 날짜 및 시간순으로 정렬하여 검색합니다.
     *
     * @param userId    사용자의 ID입니다.
     * @param startDate 범위의 시작 날짜입니다.
     * @param endDate   범위의 종료 날짜입니다.
     * @return MonthlyLedgerDetailResponse 객체 목록입니다.
     */
    @Query("SELECT new com.team2.fabackend.domain.ledger.MonthlyLedgerDetailResponse(" +
            "l.category, l.amount, l.date, l.time) " +
            "FROM Ledger l " +
            "WHERE l.user.id = :userId " +
            "AND l.type = 'EXPENSE' " +
            "AND l.date BETWEEN :startDate AND :endDate " +
            "ORDER BY l.date DESC, l.time DESC")
    List<MonthlyLedgerDetailResponse> findMonthlyLedgerDetailsByUserId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
