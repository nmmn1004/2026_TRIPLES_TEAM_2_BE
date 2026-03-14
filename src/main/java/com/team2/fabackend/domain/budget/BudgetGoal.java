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
     * Constructs a new BudgetGoal for a user with specified amounts for different categories.
     *
     * @param user            The user associated with this budget goal.
     * @param foodAmount      The budgeted amount for food.
     * @param transportAmount The budgeted amount for transport.
     * @param leisureAmount   The budgeted amount for leisure.
     * @param fixedAmount     The budgeted amount for fixed expenses.
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
     * Updates the budget goal with new amounts and recalculates the total.
     *
     * @param food      The new food budget amount.
     * @param transport The new transport budget amount.
     * @param leisure   The new leisure budget amount.
     * @param fixed     The new fixed expense budget amount.
     */
    public void update(Long food, Long transport, Long leisure, Long fixed) {
        this.foodAmount = food;
        this.transportAmount = transport;
        this.leisureAmount = leisure;
        this.fixedAmount = fixed;
        this.totalAmount = calculateTotal(food, transport, leisure, fixed);
    }

    /**
     * Calculates the total budget amount by summing all category amounts.
     *
     * @param food      The food budget amount.
     * @param transport The transport budget amount.
     * @param leisure   The leisure budget amount.
     * @param fixed     The fixed budget amount.
     * @return The total sum of all budget categories.
     */
    private Long calculateTotal(Long food, Long transport, Long leisure, Long fixed) {
        return (food != null ? food : 0L) +
                (transport != null ? transport : 0L) +
                (leisure != null ? leisure : 0L) +
                (fixed != null ? fixed : 0L);
    }
}
