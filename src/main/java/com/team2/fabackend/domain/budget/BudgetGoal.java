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

    @Builder
    public BudgetGoal(User user, Long foodAmount, Long transportAmount, Long leisureAmount, Long fixedAmount) {
        this.user = user;
        this.foodAmount = foodAmount;
        this.transportAmount = transportAmount;
        this.leisureAmount = leisureAmount;
        this.fixedAmount = fixedAmount;
        this.totalAmount = calculateTotal(foodAmount, transportAmount, leisureAmount, fixedAmount);
    }

    public void update(Long food, Long transport, Long leisure, Long fixed) {
        this.foodAmount = food;
        this.transportAmount = transport;
        this.leisureAmount = leisure;
        this.fixedAmount = fixed;
        this.totalAmount = calculateTotal(food, transport, leisure, fixed);
    }

    private Long calculateTotal(Long food, Long transport, Long leisure, Long fixed) {
        return (food != null ? food : 0L) +
                (transport != null ? transport : 0L) +
                (leisure != null ? leisure : 0L) +
                (fixed != null ? fixed : 0L);
    }
}
