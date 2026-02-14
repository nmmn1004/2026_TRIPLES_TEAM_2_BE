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

    private Long amount;      // 금액
    private String category;  // 카테고리
    private String memo;      // 메모

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private LocalDate date; // 날짜
    private LocalTime time;

    private Long goalId;

    @Column(name = "user_id")
    private Long userId;

    public void update(Long amount, String category, String memo, com.team2.fabackend.domain.ledger.TransactionType type, LocalDate date, LocalTime time) {
        this.amount = amount;
        this.category = category;
        this.memo = memo;
        this.type = type;
        this.date = date;
        this.time = time;
    }
}