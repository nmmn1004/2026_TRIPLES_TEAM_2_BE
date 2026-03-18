package com.team2.fabackend.service.Analysis;

import com.team2.fabackend.api.analysis.dto.PersonalAnalysisResponse;
import com.team2.fabackend.domain.ledger.Ledger;
import com.team2.fabackend.domain.ledger.LedgerRepository;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisService {

    private final LedgerRepository ledgerRepository;
    private final UserRepository userRepository;

    /**
     * 사용자의 소비 패턴을 다각도로 분석하여 결과를 반환합니다.
     * 분석 항목에는 이번 달 카테고리별 비중, 최근 일주일간 요일별 소비 추이, 연령대별 평균 대비 소비 비율이 포함됩니다.
     * 
     * @param userId 분석을 진행할 유저의 식별자
     * @return 카테고리별 사용량, 요일별 사용량, 또래 비교 데이터 등이 담긴 분석 응답 DTO
     */
    public PersonalAnalysisResponse getPersonalAnalysis(Long userId) {
        LocalDate now = LocalDate.now();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<Ledger> monthExpenses = ledgerRepository.findAllExpensesByUserIdBetween(
                userId, firstDayOfMonth, lastDayOfMonth);

        long totalMonthAmount = monthExpenses.stream()
                .mapToLong(Ledger::getAmount)
                .sum();
        
        List<PersonalAnalysisResponse.CategoryUsage> categoryUsageList = monthExpenses.stream()
                .collect(Collectors.groupingBy(Ledger::getCategory, Collectors.summingLong(Ledger::getAmount)))
                .entrySet().stream()
                .map(entry -> new PersonalAnalysisResponse.CategoryUsage(
                        entry.getKey(),
                        entry.getValue(),
                        totalMonthAmount > 0 ? Math.round((double)entry.getValue() / totalMonthAmount * 100 * 10) / 10.0 : 0))
                .sorted(Comparator.comparing(PersonalAnalysisResponse.CategoryUsage::getTotalAmount).reversed())
                .collect(Collectors.toList());

        LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1); 
        List<Ledger> weekExpenses = ledgerRepository.findAllExpensesByUserIdBetween(
                userId, startOfWeek, startOfWeek.plusDays(6));

        List<String> targetCategories = Arrays.asList("식비", "교통비", "여가비", "쇼핑", "기타");
        String[] dayNames = {"월", "화", "수", "목", "금", "토", "일"};

        Map<String, List<PersonalAnalysisResponse.DailyUsage>> categoryWeeklyUsage = new HashMap<>();

        for (String category : targetCategories) {
            Map<DayOfWeek, Long> dayMap = weekExpenses.stream()
                    .filter(l -> l.getCategory().equals(category))
                    .collect(Collectors.groupingBy(l -> l.getDate().getDayOfWeek(), Collectors.summingLong(Ledger::getAmount)));

            List<PersonalAnalysisResponse.DailyUsage> dailyList = new ArrayList<>();
            for (int i = 1; i <= 7; i++) {
                Long amount = dayMap.getOrDefault(DayOfWeek.of(i), 0L);
                dailyList.add(new PersonalAnalysisResponse.DailyUsage(dayNames[i - 1], amount));
            }
            categoryWeeklyUsage.put(category, dailyList);
        }

        int currentYear = now.getYear();
        int birthYear = user.getBirth().getYear();
        int age = currentYear - birthYear;
        int ageGroup = (age / 10) * 10;

        LocalDate ageStart = LocalDate.of(currentYear - ageGroup - 9, 1, 1);
        LocalDate ageEnd = LocalDate.of(currentYear - ageGroup, 12, 31);

        Double avgAmount = ledgerRepository.findAverageExpenseByAgeRange(
                ageStart, ageEnd, firstDayOfMonth, now);
        if (avgAmount == null) avgAmount = 0.0;

        long myTotalSpent = totalMonthAmount;

        double consumptionRatio = (avgAmount > 0) ? Math.round((myTotalSpent / avgAmount) * 100 * 10) / 10.0 : 0.0;

        return PersonalAnalysisResponse.builder()
                .categoryUsageList(categoryUsageList)
                .categoryWeeklyUsage(categoryWeeklyUsage)
                .ageGroup(ageGroup)
                .averageAmount(avgAmount.longValue()) 
                .myTotalSpent(myTotalSpent) 
                .consumptionRatio(consumptionRatio) 
                .build();
    }
}
