package com.team2.fabackend.domain.advice;

import com.team2.fabackend.domain.user.User;
import jakarta.persistence.*;
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
                @UniqueConstraint(columnNames = {"user_id", "createdAt"})
        }
)
public class AdviceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate createdAt;

    @Column(columnDefinition = "TEXT")
    private String adviceMessage;

    /**
     * 새로운 AdviceHistory 레코드를 생성합니다.
     *
     * @param user          사용자 객체입니다.
     * @param createdAt     레코드가 생성된 날짜입니다.
     * @param adviceMessage 조언 메시지 내용입니다.
     */
    public AdviceHistory(User user, LocalDate createdAt, String adviceMessage) {
        this.user = user;
        this.createdAt = createdAt;
        this.adviceMessage = adviceMessage;
    }
}
