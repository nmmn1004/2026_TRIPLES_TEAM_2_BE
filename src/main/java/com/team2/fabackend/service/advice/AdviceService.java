package com.team2.fabackend.service.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team2.fabackend.api.advice.dto.AdviceMessageResponse;
import com.team2.fabackend.domain.advice.AdviceHistory;
import com.team2.fabackend.domain.advice.AdviceHistoryRepository;
import com.team2.fabackend.domain.budget.BudgetGoal;
import com.team2.fabackend.domain.ledger.MonthlyLedgerDetailResponse;
import com.team2.fabackend.global.enums.ChipmunkStatus;
import com.team2.fabackend.global.enums.ResponseStatus;
import com.team2.fabackend.service.budget.BudgetReader;
import com.team2.fabackend.service.ledger.LedgerReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

            Map<String, Long> currentSpends = normalizeKeys(rawSpends);
            Map<String, Long> spendPercent = calculateSpendPercent(currentSpends, setGoal);

            List<MonthlyLedgerDetailResponse> monthlyDetails = ledgerReader.getMonthlyLedgerDetails(userId)
                    .stream()
                    .limit(20)
                    .toList();

            ObjectMapper mapper = JsonMapper.builder()
                    .addModule(new JavaTimeModule())
                    .build();

            String spendPercentJson = mapper.writeValueAsString(spendPercent);
            String currentSpendsJson = mapper.writeValueAsString(currentSpends);
            String monthlyDetailsJson = mapper.writeValueAsString(monthlyDetails);

            log.info("🧾 spendPercentJson = {}", spendPercentJson);
            log.info("🧾 currentSpendsJson = {}", currentSpendsJson);
            log.info("🧾 monthlyDetailsJson = {}", monthlyDetailsJson);
            log.info("🧠 SYSTEM PROMPT = {}", generateAdviceSystemPrompt.getTemplate());
            log.info("🧠 USER PROMPT = {}", generateAdvicePrompt.getTemplate());

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

            List<String> highlights = extractHighlights(spendPercent, monthlyDetails);

            adviceHistoryRepository.save(new AdviceHistory(userId, today, message));

            return new AdviceMessageResponse(
                    ResponseStatus.SUCCESS,
                    chipmunkStatus,
                    message,
                    highlights
            );

        } catch (Exception e) {
            log.error("❌ Advice 생성 실패 (userId={})", userId, e);

            return new AdviceMessageResponse(
                    ResponseStatus.ERROR,
                    ChipmunkStatus.CHIPMUNK_NEGATIVE,
                    "소비 분석 중 오류가 발생했어요. 잠시 후 다시 시도해주세요.",
                    List.of("분석 실패", "잠시 후 재시도")
            );
        }
    }

    private Map<String, Long> normalizeKeys(Map<String, Long> raw) {
        return raw.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toLowerCase(),
                        Map.Entry::getValue,
                        Long::sum
                ));
    }

    private Map<String, Long> calculateSpendPercent(Map<String, Long> currentSpends, BudgetGoal setGoal) {
        Map<String, Long> result = new HashMap<>();
        result.put("food", calculatePercent(currentSpends.getOrDefault("food", 0L), setGoal.getFoodAmount()));
        result.put("transport", calculatePercent(currentSpends.getOrDefault("transport", 0L), setGoal.getTransportAmount()));
        result.put("leisure", calculatePercent(currentSpends.getOrDefault("leisure", 0L), setGoal.getLeisureAmount()));
        result.put("fixed", calculatePercent(currentSpends.getOrDefault("fixed", 0L), setGoal.getFixedAmount()));
        return result;
    }

    private Long calculatePercent(long currentSpend, long setSpend) {
        if (setSpend == 0) return 0L;
        double percent = ((double) (setSpend - currentSpend) / setSpend) * 100.0;
        return Math.round(percent);
    }

    private ChipmunkStatus decideChipmunkStatus(Map<String, Long> spendPercent) {
        long min = spendPercent.values().stream()
                .min(Long::compareTo)
                .orElse(0L);
        return min < -20 ? ChipmunkStatus.CHIPMUNK_NEGATIVE : ChipmunkStatus.CHIPMUNK_POSITIVE;
    }

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