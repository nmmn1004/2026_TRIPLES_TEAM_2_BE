package com.team2.fabackend.service.goals;

import com.team2.fabackend.api.goals.dto.CategoryStatResponse;
import com.team2.fabackend.api.goals.dto.GoalAnalysisResponse;
import com.team2.fabackend.api.goals.dto.GoalRequest;
import com.team2.fabackend.api.goals.dto.GoalResponse;
import com.team2.fabackend.domain.goals.Goal;
import com.team2.fabackend.domain.goals.GoalRepository;
import com.team2.fabackend.domain.ledger.LedgerRepository;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalService {
    private final GoalRepository goalRepository;
    private final LedgerRepository ledgerRepository;
    private final UserRepository userRepository;

    // C : 목표 설정 및 저장
    @Transactional
    public Long createGoal(GoalRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        Goal goal = Goal.builder()
                .user(user)
                .title(request.getTitle())
                .category(request.getCategory())
                .targetAmount(request.getTargetAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .memo(request.getMemo())
                .currentAmount(0L)
                .build();

        goal.calculateDailyAllowance();
        return goalRepository.save(goal).getId();
    }

    // R : 목표 조회
    public List<GoalResponse> findAllGoals() {
        return goalRepository.findAll().stream().map(goal -> {
            Long totalSpent = goal.getCurrentAmount();

            Double E = goal.getDailyAllowance();
            long passedDays = java.time.temporal.ChronoUnit.DAYS.between(goal.getStartDate(), LocalDate.now());
            double cumulativeAllowance = E * Math.max(0, passedDays);

            double diff = totalSpent - cumulativeAllowance;
            long changedDays = Math.round(Math.abs(diff / E));

            String status = determineStatus(totalSpent, cumulativeAllowance);

            double successRate = calculateSuccessRate(goal, diff, changedDays);

            List<CategoryStatResponse> categoryStats = ledgerRepository.findCategoryStatsBetweenDates(
                    goal.getStartDate(), LocalDate.now());

            return GoalResponse.builder()
                    .id(goal.getId())
                    .title(goal.getTitle())
                    .targetAmount(goal.getTargetAmount())
                    .currentSpend(totalSpent)
                    .status(status)
                    .categoryStats(categoryStats)
                    .successRate(successRate)
                    .changedDays(changedDays)
                    .isDelayed(diff > 0)
                    .build();
        }).collect(Collectors.toList());
    }

    // R : 현재 진행중인 목표(유효한 목표) 조회
    public List<GoalResponse> findActiveGoals(Long userId) {
        LocalDate today = LocalDate.now();

        return goalRepository.findAllByUserId(userId).stream()
                .filter(goal -> !today.isBefore(goal.getStartDate()) && !today.isAfter(goal.getEndDate()))
                .map(goal -> {
                    Long totalSpent = goal.getCurrentAmount();

                    Double E = goal.getDailyAllowance();
                    long passedDays = java.time.temporal.ChronoUnit.DAYS.between(goal.getStartDate(), LocalDate.now());
                    double cumulativeAllowance = E * Math.max(0, passedDays);

                    double diff = totalSpent - cumulativeAllowance;
                    long changedDays = Math.round(Math.abs(diff / E));

                    String status = determineStatus(totalSpent, cumulativeAllowance);

                    return GoalResponse.builder()
                            .id(goal.getId())
                            .title(goal.getTitle())
                            .targetAmount(goal.getTargetAmount())
                            .currentSpend(totalSpent)
                            .status(status)
                            .successRate(calculateSuccessRate(goal, diff, changedDays))
                            .changedDays(changedDays)
                            .isDelayed(diff > 0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // U : 목표 수정
    @Transactional
    public void updateGoal(Long id, GoalRequest request) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("목표가 없습니다. id=" + id));
        goal.update(request.getTitle(), request.getTargetAmount(), request.getStartDate(), request.getEndDate(), request.getMemo(), request.getCategory());
    }

    // D : 목표 삭제
    @Transactional
    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }

    // analyzeGoal : 개별 목표 상세 분석
    public GoalAnalysisResponse analyzeGoal(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("목표를 찾을 수 없습니다. id=" + id));

        Long totalSpent = goal.getCurrentAmount();

        Double E = goal.getDailyAllowance();
        long passedDays = java.time.temporal.ChronoUnit.DAYS.between(goal.getStartDate(), LocalDate.now().plusDays(1));

        double diff = totalSpent - (E * passedDays);
        long changedDays = Math.round(Math.abs(diff / E));

        long totalPeriod = java.time.temporal.ChronoUnit.DAYS.between(goal.getStartDate(), goal.getEndDate());
        if (totalPeriod <= 0) totalPeriod = 1;
        long delayedDaysForRate = (diff > 0) ? changedDays : 0;
        double successRate = Math.max(0, Math.round(((double) (totalPeriod - delayedDaysForRate) / totalPeriod * 100) * 10) / 10.0);

        String type;
        String message;

        if (diff > 0) {
            type = "DELAYED";
            message = String.format("목표 기간 중 소비로 인해 약 %d일이 사라졌어요. 달성까지 %d일이 더 필요해요.", changedDays, changedDays);
        } else {
            type = "SHORTENED";
            message = String.format("오늘의 절약으로 목표 성공률을 %.1f%%로 유지하고 있어요! 목표일을 %d일 단축시켰습니다.", successRate, changedDays);
        }

        return GoalAnalysisResponse.builder()
                .goalId(goal.getId())
                .changedDays(changedDays)
                .type(type)
                .successRate(successRate)
                .analysisMessage(message)
                .build();
    }

    private String determineStatus(Long spent, double allowance) {
        if (spent > allowance * 1.1) return "위험";
        if (spent > allowance) return "주의";
        return "안전";
    }

    private double calculateSuccessRate(Goal goal, double diff, long changedDays) {
        long totalPeriod = java.time.temporal.ChronoUnit.DAYS.between(goal.getStartDate(), goal.getEndDate());
        if (totalPeriod <= 0) totalPeriod = 1;

        long delayedDaysForRate = (diff > 0) ? changedDays : 0;

        return Math.max(0, Math.round(((double) (totalPeriod - delayedDaysForRate) / totalPeriod * 100) * 10) / 10.0);
    }
}