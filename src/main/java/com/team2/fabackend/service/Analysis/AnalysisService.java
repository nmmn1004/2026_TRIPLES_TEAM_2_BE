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
    private final UserRepository userRepository; // 유저 정보(생일) 조회를 위해 추가

    public PersonalAnalysisResponse getPersonalAnalysis(Long userId) {
        LocalDate now = LocalDate.now();

        // 0. 유저 정보 조회 (연령대 계산용)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

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

        // 2. 일주일간 카테고리별 요일 소비 통계 (점 그래프용)
        LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1); // 이번주 월요일
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

        // 3. 연령별 소비 패턴 분석 로직
        // 유저 나이 계산 및 연령대 판별
        int currentYear = now.getYear();
        int birthYear = user.getBirth().getYear();
        int age = currentYear - birthYear;
        int ageGroup = (age / 10) * 10;

        // 해당 연령대의 출생 연도 범위 설정
        LocalDate ageStart = LocalDate.of(currentYear - ageGroup - 9, 1, 1);
        LocalDate ageEnd = LocalDate.of(currentYear - ageGroup, 12, 31);

        // 또래 평균 지출액 조회
        Double avgAmount = ledgerRepository.findAverageExpenseByAgeRange(
                ageStart, ageEnd, firstDayOfMonth, now);
        if (avgAmount == null) avgAmount = 0.0;

        // 나의 이번 달 총 지출액
        long myTotalSpent = totalMonthAmount;

        // 평균 대비 비율 계산 (%)
        double consumptionRatio = (avgAmount > 0) ? Math.round((myTotalSpent / avgAmount) * 100 * 10) / 10.0 : 0.0;

        return PersonalAnalysisResponse.builder()
                .categoryUsageList(categoryUsageList)
                .categoryWeeklyUsage(categoryWeeklyUsage)
                .ageGroup(ageGroup)
                .averageAmount(avgAmount.longValue()) // 또래 평균액
                .myTotalSpent(myTotalSpent) // 나의 총 지출액
                .consumptionRatio(consumptionRatio) // 비율
                .build();
    }
}