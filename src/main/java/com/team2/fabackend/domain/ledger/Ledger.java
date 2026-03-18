package com.team2.fabackend.domain.ledger;

import com.team2.fabackend.domain.user.User;
import jakarta.persistence.*;
import com.team2.fabackend.domain.ledger.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ledger {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;      
    private String category;  
    private String memo;      

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private LocalDate date; 
    private LocalTime time;

    private Long goalId;

    @Column(name = "user_id")
    private Long userId;

    /**
     * 가계부 항목을 새로운 정보로 업데이트합니다.
     *
     * @param amount   새로운 금액입니다.
     * @param category 새로운 카테고리입니다.
     * @param memo     새로운 메모입니다.
     * @param type     새로운 거래 유형입니다.
     * @param date     새로운 날짜입니다.
     * @param time     새로운 시간입니다.
     */
    public void update(Long amount, String category, String memo, com.team2.fabackend.domain.ledger.TransactionType type, LocalDate date, LocalTime time) {
        this.amount = amount;
        this.category = category;
        this.memo = memo;
        this.type = type;
        this.date = date;
        this.time = time;
    }
}
