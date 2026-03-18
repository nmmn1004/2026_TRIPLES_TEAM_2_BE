package com.team2.fabackend.domain.budget;

import com.team2.fabackend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "budget_goals")
public class BudgetGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Long foodAmount;
    private Long transportAmount;
    private Long leisureAmount;
    private Long fixedAmount;

    private Long totalAmount;

    /**
     * 서로 다른 카테고리에 대해 지정된 금액으로 사용자를 위한 새로운 BudgetGoal을 생성합니다.
     *
     * @param user            이 예산 목표와 연관된 사용자입니다.
     * @param foodAmount      식비에 대한 예산 금액입니다.
     * @param transportAmount 교통비에 대한 예산 금액입니다.
     * @param leisureAmount   여가비에 대한 예산 금액입니다.
     * @param fixedAmount     고정 지출에 대한 예산 금액입니다.
     */
    @Builder
    public BudgetGoal(User user, Long foodAmount, Long transportAmount, Long leisureAmount, Long fixedAmount) {
        this.user = user;
        this.foodAmount = foodAmount;
        this.transportAmount = transportAmount;
        this.leisureAmount = leisureAmount;
        this.fixedAmount = fixedAmount;
        this.totalAmount = calculateTotal(foodAmount, transportAmount, leisureAmount, fixedAmount);
    }

    /**
     * 예산 목표를 새로운 금액으로 업데이트하고 총액을 다시 계산합니다.
     *
     * @param food      새로운 식비 예산 금액입니다.
     * @param transport 새로운 교통비 예산 금액입니다.
     * @param leisure   새로운 여가비 예산 금액입니다.
     * @param fixed     새로운 고정 지출 예산 금액입니다.
     */
    public void update(Long food, Long transport, Long leisure, Long fixed) {
        this.foodAmount = food;
        this.transportAmount = transport;
        this.leisureAmount = leisure;
        this.fixedAmount = fixed;
        this.totalAmount = calculateTotal(food, transport, leisure, fixed);
    }

    /**
     * 모든 카테고리 금액을 합산하여 총 예산 금액을 계산합니다.
     *
     * @param food      식비 예산 금액입니다.
     * @param transport 교통비 예산 금액입니다.
     * @param leisure   여가비 예산 금액입니다.
     * @param fixed     고정 예산 금액입니다.
     * @return 모든 예산 카테고리의 총합입니다.
     */
    private Long calculateTotal(Long food, Long transport, Long leisure, Long fixed) {
        return (food != null ? food : 0L) +
                (transport != null ? transport : 0L) +
                (leisure != null ? leisure : 0L) +
                (fixed != null ? fixed : 0L);
    }
}
