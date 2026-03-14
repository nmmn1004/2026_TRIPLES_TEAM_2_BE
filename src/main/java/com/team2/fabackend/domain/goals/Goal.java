package com.team2.fabackend.domain.goals;

import com.team2.fabackend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Long targetAmount; 

    @Builder.Default
    private Long currentAmount = 0L;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private String memo;

    private Double dailyAllowance; 

    /**
     * Adds an amount to the current accumulated amount for this goal.
     *
     * @param amount The amount to add.
     */
    public void addCurrentAmount(Long amount) {
        if (this.currentAmount == null) {
            this.currentAmount = 0L;
        }
        this.currentAmount += amount;
    }

    /**
     * Updates the goal's information and recalculates the daily allowance.
     *
     * @param title        The new title.
     * @param targetAmount The new target amount.
     * @param startDate    The new start date.
     * @param endDate      The new end date.
     * @param memo         The new memo.
     * @param category     The new category.
     */
    public void update(String title, Long targetAmount, LocalDate startDate, LocalDate endDate, String memo, String category) {
        this.title = title;
        this.targetAmount = targetAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.memo = memo;
        this.category = category;
        calculateDailyAllowance();
    }

    /**
     * Calculates the daily consumption allowance based on the target amount and the goal's duration.
     */
    public void calculateDailyAllowance() {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) days = 1; 
        this.dailyAllowance = (double) this.targetAmount / days;
    }

}
