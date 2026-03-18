package com.team2.fabackend.domain.advice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "advice_history",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userId", "createdAt"})
        }
)
public class AdviceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDate createdAt;

    @Column(columnDefinition = "TEXT")
    private String adviceMessage;

    /**
     * 새로운 AdviceHistory 레코드를 생성합니다.
     *
     * @param userId        사용자의 ID입니다.
     * @param createdAt     레코드가 생성된 날짜입니다.
     * @param adviceMessage 조언 메시지 내용입니다.
     */
    public AdviceHistory(Long userId, LocalDate createdAt, String adviceMessage) {
        this.userId = userId;
        this.createdAt = createdAt;
        this.adviceMessage = adviceMessage;
    }
}
