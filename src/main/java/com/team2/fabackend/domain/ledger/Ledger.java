package com.team2.fabackend.domain.ledger;

import com.team2.fabackend.domain.user.User; // 유저 엔티티 위치 확인 필요
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

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
    private LocalDateTime transactionDate; // 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}