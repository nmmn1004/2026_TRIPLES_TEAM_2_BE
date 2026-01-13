package com.team2.fabackend.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class PhoneVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;
    private String code;
    private boolean verified;
    private LocalDateTime expiredAt;

    @Builder
    public PhoneVerification(String phoneNumber, String code, LocalDateTime expiredAt) {
        this.phoneNumber = phoneNumber;
        this.code = code;
        this.expiredAt = expiredAt;
        this.verified = false;
    }

    public void verify() {
        this.verified = true;
    }
}
