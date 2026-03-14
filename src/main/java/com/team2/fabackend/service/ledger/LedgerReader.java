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
     * Retrieves a map of monthly category sums for a user.
     *
     * @param userId The ID of the user.
     * @return A map where the key is the category and the value is the total amount.
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
     * Retrieves detailed monthly ledger entries for a user.
     *
     * @param userId The ID of the user.
     * @return A list of detailed monthly ledger entries.
     */
    public List<MonthlyLedgerDetailResponse> getMonthlyLedgerDetails(Long userId) {
        YearMonth yearMonth = YearMonth.now(ZoneId.of("Asia/Seoul"));

        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        return ledgerRepository.findMonthlyLedgerDetailsByUserId(userId, start, end);
    }

}
