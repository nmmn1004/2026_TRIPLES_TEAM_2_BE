package com.team2.fabackend.service.analysis; // 패키지 경로 확인해주세요!

import com.team2.fabackend.api.analysis.dto.PersonalAnalysisResponse;
import com.team2.fabackend.domain.ledger.Ledger;
import com.team2.fabackend.domain.ledger.LedgerRepository;
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

    public PersonalAnalysisResponse getPersonalAnalysis(Long userId) {
        LocalDate now = LocalDate.now();

        // 1. 이번 달 카테고리별 비중 계산 (원형 그래프용)
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

        // 2. 일주일간 카테고리별 요일 소비 통계 (점 그래프용 탭 데이터)
        LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1); // 이번주 월요일
        List<Ledger> weekExpenses = ledgerRepository.findAllExpensesByUserIdBetween(
                userId, startOfWeek, startOfWeek.plusDays(6));

        // 기획안에 있는 주요 카테고리 정의
        List<String> targetCategories = Arrays.asList("식비", "교통비", "여가비", "쇼핑", "기타");
        String[] dayNames = {"월", "화", "수", "목", "금", "토", "일"};

        Map<String, List<PersonalAnalysisResponse.DailyUsage>> categoryWeeklyUsage = new HashMap<>();

        for (String category : targetCategories) {
            // 해당 카테고리에 속하는 지출만 필터링하여 요일별 합산
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

        return PersonalAnalysisResponse.builder()
                .categoryUsageList(categoryUsageList)
                .categoryWeeklyUsage(categoryWeeklyUsage) // Map 형태로 전달
                .build();
    }
}