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
    private Long targetAmount; // 목표 금액

    @Builder.Default
    private Long currentAmount = 0L;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private String memo;

    private Double dailyAllowance; // 일일 소비 허용치

    public void addCurrentAmount(Long amount) {
        if (this.currentAmount == null) {
            this.currentAmount = 0L;
        }
        this.currentAmount += amount;
    }

    public void update(String title, Long targetAmount, LocalDate startDate, LocalDate endDate, String memo, String category) {
        this.title = title;
        this.targetAmount = targetAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.memo = memo;
        this.category = category;
        calculateDailyAllowance();
    }

    public void calculateDailyAllowance() {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) days = 1; // 0으로 나누기 방지
        this.dailyAllowance = (double) this.targetAmount / days;
    }

}