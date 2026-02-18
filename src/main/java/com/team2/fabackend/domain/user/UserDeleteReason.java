package com.team2.fabackend.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_delete_reasons")
public class UserDeleteReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate birthDate;

    private String reason;
    private String reason_detail;

    @Builder
    protected UserDeleteReason(
            LocalDate birthDate,
            String reason,
            String reason_detail
    ) {
        this.birthDate = birthDate;
        this.reason = reason;
        this.reason_detail = reason_detail;
    }
}
