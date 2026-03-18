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

    /**
     * 사용자의 새로운 저축 목표를 생성하고 저장합니다.
     * 
     * @param request 저축 목표 생성 요청 정보 (제목, 목표 금액, 시작/종료일 등)
     * @param userId 유저 식별자
     * @return 생성된 저축 목표의 ID
     */
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

    /**
     * 시스템에 등록된 모든 저축 목표를 조회합니다.
     * 
     * @return 모든 저축 목표 응답 리스트
     */
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

    /**
     * 특정 사용자의 현재 진행 중인(활성화된) 저축 목표 리스트를 조회합니다.
     * 
     * @param userId 유저 식별자
     * @return 활성화된 저축 목표 응답 리스트
     */
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

    /**
     * 기존 저축 목표 정보를 수정합니다.
     * 
     * @param id 수정할 저축 목표의 식별자
     * @param request 수정할 저축 목표 정보
     */
    @Transactional
    public void updateGoal(Long id, GoalRequest request) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("목표가 없습니다. id=" + id));
        goal.update(request.getTitle(), request.getTargetAmount(), request.getStartDate(), request.getEndDate(), request.getMemo(), request.getCategory());
    }

    /**
     * 특정 저축 목표를 삭제합니다.
     * 
     * @param id 삭제할 저축 목표의 식별자
     */
    @Transactional
    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }

    /**
     * 특정 저축 목표의 달성도 및 진행 상태를 상세 분석합니다.
     * 
     * @param id 분석할 저축 목표의 식별자
     * @return 분석 결과 정보 (달성률, 변동 일수, 분석 메시지 등)
     */
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

    /**
     * 누적 지출액과 허용 예산을 비교하여 목표의 현재 상태(안전, 주의, 위험)를 판별합니다.
     * 
     * @param spent 현재까지의 누적 지출액
     * @param allowance 현재까지 허용된 누적 예산
     * @return 상태 문자열 (위험, 주의, 안전)
     */
    private String determineStatus(Long spent, double allowance) {
        if (spent > allowance * 1.1) return "위험";
        if (spent > allowance) return "주의";
        return "안전";
    }

    /**
     * 목표의 현재 달성 성공률을 계산합니다.
     * 
     * @param goal 저축 목표 엔티티
     * @param diff 누적 지출액과 허용 예산의 차이
     * @param changedDays 변동된 일수
     * @return 계산된 성공률 (%)
     */
    private double calculateSuccessRate(Goal goal, double diff, long changedDays) {
        long totalPeriod = java.time.temporal.ChronoUnit.DAYS.between(goal.getStartDate(), goal.getEndDate());
        if (totalPeriod <= 0) totalPeriod = 1;

        long delayedDaysForRate = (diff > 0) ? changedDays : 0;

        return Math.max(0, Math.round(((double) (totalPeriod - delayedDaysForRate) / totalPeriod * 100) * 10) / 10.0);
    }
}
