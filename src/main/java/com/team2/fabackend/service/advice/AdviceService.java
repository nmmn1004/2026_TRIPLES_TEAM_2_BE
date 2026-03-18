package com.team2.fabackend.service.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team2.fabackend.api.advice.dto.AdviceMessageResponse;
import com.team2.fabackend.domain.advice.AdviceHistory;
import com.team2.fabackend.domain.advice.AdviceHistoryRepository;
import com.team2.fabackend.domain.budget.BudgetGoal;
import com.team2.fabackend.domain.ledger.MonthlyLedgerDetailResponse;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.global.enums.ChipmunkStatus;
import com.team2.fabackend.global.enums.ResponseStatus;
import com.team2.fabackend.service.budget.BudgetReader;
import com.team2.fabackend.service.ledger.LedgerReader;
import com.team2.fabackend.service.user.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdviceService {
    private final ChatClient chatClient;
    private final PromptTemplate generateAdvicePrompt;
    private final PromptTemplate generateAdviceSystemPrompt;

    private final AdviceHistoryRepository adviceHistoryRepository;
    private final BudgetReader budgetReader;
    private final LedgerReader ledgerReader;
    private final UserReader userReader;

    /**
     * 사용자의 예산 설정과 지출 내역을 분석하여 AI 기반의 맞춤형 소비 조언 메시지를 생성합니다.
     * 당일 이미 생성된 조언이 있다면 이를 반환하고, 데이터가 부족하면 기본 메시지를 반환합니다.
     * 
     * @param userId 조언을 생성할 유저의 식별자
     * @return 조언 메시지, 상태, 하이라이트 정보 등이 담긴 응답 DTO
     */
    @Transactional
    public AdviceMessageResponse generateAdvice(Long userId) {
        try {
            LocalDate today = LocalDate.now();

            if (adviceHistoryRepository.existsByUserIdAndCreatedAt(userId, today)) {
                return new AdviceMessageResponse(
                        ResponseStatus.EXIST,
                        ChipmunkStatus.CHIPMUNK_POSITIVE,
                        adviceHistoryRepository.findByUserIdAndCreatedAt(userId, today)
                                .map(AdviceHistory::getAdviceMessage)
                                .orElse("오늘의 조언을 불러오지 못했어요."),
                        Collections.emptyList()
                );
            }

            BudgetGoal setGoal = budgetReader.getById(userId);
            Map<String, Long> rawSpends = ledgerReader.getMonthlyCategorySumMap(userId);
            List<MonthlyLedgerDetailResponse> monthlyDetails = ledgerReader.getMonthlyLedgerDetails(userId)
                    .stream()
                    .limit(20)
                    .toList();

            boolean hasNoBudget = setGoal == null ||
                    (setGoal.getFoodAmount() == 0 && setGoal.getTransportAmount() == 0 &&
                            setGoal.getLeisureAmount() == 0 && setGoal.getFixedAmount() == 0);
            boolean hasNoSpends = rawSpends == null || rawSpends.isEmpty() || monthlyDetails.isEmpty();

            if (hasNoBudget || hasNoSpends) {
                return new AdviceMessageResponse(
                        ResponseStatus.SUCCESS,
                        ChipmunkStatus.CHIPMUNK_POSITIVE,
                        "아직 분석할 소비 내역이나 예산이 부족해요. 열심히 가계부를 써보아요! 📝",
                        Collections.emptyList()
                );
            }

            Map<String, Long> currentSpends = normalizeKeys(rawSpends);
            Map<String, Long> spendPercent = calculateSpendPercent(currentSpends, setGoal);

            ObjectMapper mapper = JsonMapper.builder()
                    .addModule(new JavaTimeModule())
                    .build();

            String spendPercentJson = mapper.writeValueAsString(spendPercent);
            String currentSpendsJson = mapper.writeValueAsString(currentSpends);
            String monthlyDetailsJson = mapper.writeValueAsString(monthlyDetails);

            ChipmunkStatus chipmunkStatus = decideChipmunkStatus(spendPercent);

            String message = chatClient.prompt()
                    .system(generateAdviceSystemPrompt.getTemplate())
                    .user(u -> u
                            .text(generateAdvicePrompt.getTemplate())
                            .param("chipmunkStatus", chipmunkStatus.name())
                            .param("spendPercentJson", spendPercentJson)
                            .param("currentSpendsJson", currentSpendsJson)
                            .param("monthlyDetailsJson", monthlyDetailsJson)
                    )
                    .call()
                    .content();

            if (message != null) {
                message = message
                        .replaceAll("(?s)```text", "")
                        .replaceAll("```", "")
                        .trim();
            }

            List<String> highlights = extractHighlights(spendPercent, monthlyDetails);

            User user = userReader.findById(userId);
            adviceHistoryRepository.save(new AdviceHistory(user, today, message));

            return new AdviceMessageResponse(
                    ResponseStatus.SUCCESS,
                    chipmunkStatus,
                    message,
                    highlights
            );

        } catch (Exception e) {
            return new AdviceMessageResponse(
                    ResponseStatus.ERROR,
                    ChipmunkStatus.CHIPMUNK_NEGATIVE,
                    "소비 분석 중 오류가 발생했어요. 잠시 후 다시 시도해주세요.",
                    List.of("분석 실패", "잠시 후 재시도")
            );
        }
    }

    /**
     * 카테고리 맵의 키값을 소문자로 정규화하여 처리합니다.
     * 
     * @param raw 원본 지출 맵
     * @return 소문자 키를 가진 지출 맵
     */
    private Map<String, Long> normalizeKeys(Map<String, Long> raw) {
        return raw.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toLowerCase(),
                        Map.Entry::getValue,
                        Long::sum
                ));
    }

    /**
     * 예산 목표 대비 현재 지출 비율을 각 카테고리별로 계산합니다.
     * 
     * @param currentSpends 현재 지출 현황
     * @param setGoal 설정된 예산 목표
     * @return 카테고리별 예산 잔여/초과 비율 맵
     */
    private Map<String, Long> calculateSpendPercent(Map<String, Long> currentSpends, BudgetGoal setGoal) {
        Map<String, Long> result = new HashMap<>();
        result.put("food", calculatePercent(currentSpends.getOrDefault("food", 0L), setGoal.getFoodAmount()));
        result.put("transport", calculatePercent(currentSpends.getOrDefault("transport", 0L), setGoal.getTransportAmount()));
        result.put("leisure", calculatePercent(currentSpends.getOrDefault("leisure", 0L), setGoal.getLeisureAmount()));
        result.put("fixed", calculatePercent(currentSpends.getOrDefault("fixed", 0L), setGoal.getFixedAmount()));
        return result;
    }

    /**
     * 특정 금액 간의 비율을 백분율로 계산합니다.
     * 
     * @param currentSpend 현재 지출액
     * @param setSpend 설정 예산액
     * @return 계산된 백분율
     */
    private Long calculatePercent(long currentSpend, long setSpend) {
        if (setSpend == 0) return 0L;
        double percent = ((double) (setSpend - currentSpend) / setSpend) * 100.0;
        return Math.round(percent);
    }

    /**
     * 지출 비율에 따라 앱의 캐릭터(다람쥐) 상태를 결정합니다.
     * 
     * @param spendPercent 카테고리별 지출 비율
     * @return 결정된 캐릭터 상태 (긍정/부정)
     */
    private ChipmunkStatus decideChipmunkStatus(Map<String, Long> spendPercent) {
        long min = spendPercent.values().stream()
                .min(Long::compareTo)
                .orElse(0L);
        return min < -20 ? ChipmunkStatus.CHIPMUNK_NEGATIVE : ChipmunkStatus.CHIPMUNK_POSITIVE;
    }

    /**
     * 지출 데이터와 상세 내역에서 사용자에게 보여줄 주요 특징(하이라이트)을 추출합니다.
     * 
     * @param spendPercent 카테고리별 지출 비율
     * @param monthlyDetails 월간 상세 내역
     * @return 추출된 하이라이트 문자열 리스트
     */
    private List<String> extractHighlights(Map<String, Long> spendPercent,
                                           List<MonthlyLedgerDetailResponse> monthlyDetails) {

        List<String> highlights = new ArrayList<>();

        spendPercent.forEach((category, percent) -> {
            if (percent < 0) {
                highlights.add(category + " 예산 초과 " + Math.abs(percent) + "%");
            }
        });

        long lateNightCount = monthlyDetails.stream()
                .filter(d -> d.getTime() != null)
                .filter(d -> d.getTime().isAfter(LocalTime.of(21, 0)))
                .count();

        if (lateNightCount >= 3) {
            highlights.add("야간 소비 빈도 높음");
        }

        return highlights;
    }
}
