package com.team2.fabackend.service.ledger;

import com.team2.fabackend.domain.ledger.Ledger;
import com.team2.fabackend.domain.ledger.LedgerRepository;
import com.team2.fabackend.domain.ledger.MonthlyCategorySumLedger;
import com.team2.fabackend.domain.ledger.MonthlyLedgerDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LedgerReader {
    private final LedgerRepository ledgerRepository;

    /**
     * 사용자의 월별 카테고리 합계 맵을 조회합니다.
     *
     * @param userId 사용자의 ID.
     * @return 키는 카테고리, 값은 총 금액인 맵.
     */
    public Map<String, Long> getMonthlyCategorySumMap(Long userId) {
        YearMonth yearMonth = YearMonth.now(ZoneId.of("Asia/Seoul"));
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<MonthlyCategorySumLedger> stats =
                ledgerRepository.findMonthlyCategorySumByUserId(userId, start, end);

        return stats.stream()
                .collect(Collectors.toMap(
                        MonthlyCategorySumLedger::getCategory,
                        MonthlyCategorySumLedger::getTotalAmount
                ));
    }

    /**
     * 사용자의 상세 월별 가계부 내역을 조회합니다.
     *
     * @param userId 사용자의 ID.
     * @return 상세 월별 가계부 내역 리스트.
     */
    public List<MonthlyLedgerDetailResponse> getMonthlyLedgerDetails(Long userId) {
        YearMonth yearMonth = YearMonth.now(ZoneId.of("Asia/Seoul"));

        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        return ledgerRepository.findMonthlyLedgerDetailsByUserId(userId, start, end);
    }

}
