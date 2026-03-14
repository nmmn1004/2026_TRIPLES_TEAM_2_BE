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

    /**
     * 사용자의 설문 기반 예산 요청을 받아 각 카테고리별 예산을 계산하고 저장하거나 업데이트합니다.
     * 
     * @param req 예산 설정 요청 DTO (각 카테고리별 옵션 포함)
     * @param userId 유저 식별자
     * @return 저장된 예산 목표의 ID
     */
    @Transactional
    public Long saveBudget(BudgetRequest req, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        long food = calculateFood(req);
        long transport = calculateTransport(req);
        long leisure = calculateLeisure(req);
        long fixed = calculateFixed(req);
        
        BudgetGoal budgetGoal = budgetRepository.findByUserId(userId)
                .map(existing -> {
                    existing.update(food, transport, leisure, fixed);
                    return existing;
                })
                .orElseGet(() -> BudgetGoal.builder()
                            .user(user)
                            .foodAmount(food)
                            .transportAmount(transport)
                            .leisureAmount(leisure)
                            .fixedAmount(fixed)
                            .build());

        return budgetRepository.save(budgetGoal).getId();
    }

    /**
     * 특정 사용자의 예산 목표 정보를 조회합니다.
     * 
     * @param userId 유저 식별자
     * @return 사용자의 예산 목표 응답 DTO
     */
    public BudgetResponse getBudget(Long userId) {
        BudgetGoal goal = budgetRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("설정된 예산 목표가 없습니다. 유저 ID: " + userId));

        return new BudgetResponse(goal);
    }

    /**
     * 사용자의 예산 카테고리별 금액을 직접 수정합니다.
     * 
     * @param userId 유저 식별자
     * @param req 수정할 금액이 담긴 DTO
     * @return 수정된 예산 목표의 ID
     */
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

    /**
     * 설문 옵션을 기반으로 식비 예산을 계산합니다.
     * 
     * @param req 예산 설정 요청 DTO
     * @return 계산된 식비 예산 총액
     */
    private long calculateFood(BudgetRequest req) {
        long daily = switch (req.getFoodDailyOption()) {
            case 1 -> 75000; case 2 -> 225000; case 3 -> 450000; case 4 -> 900000; default -> 0;
        };
        long delivery = switch (req.getDeliveryFreqOption()) {
            case 2 -> 88000; case 3 -> 180000; default -> 0;
        };
        long dessert = switch (req.getDessertCostOption()) {
            case 1 -> 10000; case 2 -> 30000; case 3 -> 60000; case 4 -> 120000; default -> 0;
        };
        return daily + delivery + dessert;
    }

    /**
     * 설문 옵션을 기반으로 교통비 예산을 계산합니다.
     * 
     * @param req 예산 설정 요청 DTO
     * @return 계산된 교통비 예산 총액
     */
    private long calculateTransport(BudgetRequest req) {
        long base = switch (req.getTransportMonthlyOption()) {
            case 1 -> 15000; case 2 -> 35000; case 3 -> 65000; case 4 -> 100000; default -> 0;
        };
        long taxi = switch (req.getTaxiFreqOption()) {
            case 2 -> 40000; case 3 -> 100000; default -> 0;
        };
        return base + taxi;
    }

    /**
     * 설문 옵션을 기반으로 여가비 예산을 계산합니다.
     * 
     * @param req 예산 설정 요청 DTO
     * @return 계산된 여가비 예산 총액
     */
    private long calculateLeisure(BudgetRequest req) {
        long hobby = switch (req.getHobbyCostOption()) {
            case 1 -> 20000; case 2 -> 40000; case 3 -> 60000; case 4 -> 900000; default -> 0;
        };
        long subscription = (long) req.getContentFreqOption() * 12000;
        return hobby + subscription;
    }

    /**
     * 설문 옵션을 기반으로 고정 지출 예산을 계산합니다.
     * 
     * @param req 예산 설정 요청 DTO
     * @return 계산된 고정 지출 예산 총액
     */
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
