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
     * 이 목표에 대해 현재 누적된 금액에 금액을 추가합니다.
     *
     * @param amount 추가할 금액입니다.
     */
    public void addCurrentAmount(Long amount) {
        if (this.currentAmount == null) {
            this.currentAmount = 0L;
        }
        this.currentAmount += amount;
    }

    /**
     * 목표 정보를 업데이트하고 일일 허용치를 다시 계산합니다.
     *
     * @param title        새로운 제목입니다.
     * @param targetAmount 새로운 목표 금액입니다.
     * @param startDate    새로운 시작 날짜입니다.
     * @param endDate      새로운 종료 날짜입니다.
     * @param memo         새로운 메모입니다.
     * @param category     새로운 카테고리입니다.
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
     * 목표 금액과 목표 기간을 기준으로 일일 소비 허용치를 계산합니다.
     */
    public void calculateDailyAllowance() {
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) days = 1; 
        this.dailyAllowance = (double) this.targetAmount / days;
    }

}
