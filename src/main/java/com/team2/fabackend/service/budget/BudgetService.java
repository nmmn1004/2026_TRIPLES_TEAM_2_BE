package com.team2.fabackend.service.budget;

import com.team2.fabackend.api.budget.dto.BudgetRequest;
import com.team2.fabackend.api.budget.dto.BudgetResponse;
import com.team2.fabackend.api.budget.dto.BudgetUpdateRequest;
import com.team2.fabackend.domain.budget.BudgetGoal;
import com.team2.fabackend.domain.budget.BudgetRepository;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long saveBudget(BudgetRequest req, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 1. 각 카테고리별 금액 계산
        long food = calculateFood(req);
        long transport = calculateTransport(req);
        long leisure = calculateLeisure(req);
        long fixed = calculateFixed(req);

        // 2. 저장 or 업데이트
        BudgetGoal budgetGoal = budgetRepository.findByUserId(userId)
                .map(existing -> {
                    existing.update(food, transport, leisure, fixed);
                    return existing;
                })
                .orElse(BudgetGoal.builder()
                        .user(user)
                        .foodAmount(food)
                        .transportAmount(transport)
                        .leisureAmount(leisure)
                        .fixedAmount(fixed)
                        .build());

        return budgetRepository.save(budgetGoal).getId();
    }

    public BudgetResponse getBudget(Long userId) {
        BudgetGoal goal = budgetRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("설정된 예산 목표가 없습니다. 유저 ID: " + userId));

        return new BudgetResponse(goal);
    }

    //U
    @Transactional
    public Long updateBudgetAmounts(Long userId, BudgetUpdateRequest req) {
        BudgetGoal goal = budgetRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 예산 목표가 없습니다."));

        goal.update(
                req.getFoodAmount(),
                req.getTransportAmount(),
                req.getLeisureAmount(),
                req.getFixedAmount()
        );

        return goal.getId();
    }

    //카테고리별 계산 로직
    private long calculateFood(BudgetRequest req) {
        // 1) 하루 식비
        long daily = switch (req.getFoodDailyOption()) {
            case 1 -> 75000; case 2 -> 225000; case 3 -> 450000; case 4 -> 900000; default -> 0;
        };
        // 2) 배달비
        long delivery = switch (req.getDeliveryFreqOption()) {
            case 2 -> 88000; case 3 -> 180000; default -> 0;
        };
        // 3) 카페/디저트
        long dessert = switch (req.getDessertCostOption()) {
            case 1 -> 10000; case 2 -> 30000; case 3 -> 60000; case 4 -> 120000; default -> 0;
        };
        return daily + delivery + dessert;
    }

    private long calculateTransport(BudgetRequest req) {
        // 교통비 구간별 평균값 적용
        long base = switch (req.getTransportMonthlyOption()) {
            case 1 -> 15000; case 2 -> 35000; case 3 -> 65000; case 4 -> 100000; default -> 0;
        };
        // 택시 빈도 가산 (주 1~2회: 월 4만원, 주 3회 이상: 월 10만원 가정)
        long taxi = switch (req.getTaxiFreqOption()) {
            case 2 -> 40000; case 3 -> 100000; default -> 0;
        };
        return base + taxi;
    }

    private long calculateLeisure(BudgetRequest req) {
        long hobby = switch (req.getHobbyCostOption()) {
            case 1 -> 20000; case 2 -> 40000; case 3 -> 60000; case 4 -> 900000; default -> 0;
        };
        // 콘텐츠 구독료 (개당 1.2만 원 가정)
        long subscription = (long) req.getContentFreqOption() * 12000;
        return hobby + subscription;
    }

    private long calculateFixed(BudgetRequest req) {
        long fixed = switch (req.getFixedMonthlyOption()) {
            case 2 -> 40000; case 3 -> 60000; case 4 -> 100000; default -> 0;
        };
        long residence = switch (req.getResidenceCostOption()) {
            case 2 -> 150000; case 3 -> 300000; case 4 -> 500000; default -> 0;
        };
        long phone = switch (req.getCommunicationCostOption()) {
            case 1 -> 20000; case 2 -> 45000; case 3 -> 75000; case 4 -> 110000; default -> 0;
        };
        return fixed + residence + phone;
    }
}