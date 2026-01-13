package com.team2.fabackend.service.PhoneVerification;

import com.team2.fabackend.domain.user.PhoneVerification;
import com.team2.fabackend.service.user.UserReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhoneVerificationService {
    private final PhoneVerificationReader phoneVerificationReader;
    private final PhoneVerificationWriter phoneVerificationWriter;
    private final UserReader userReader;

    @Transactional
    public void sendCode(String phoneNumber) {
        if (userReader.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("이미 가입된 전화번호입니다.");
        }

        String code = generateCode();

        PhoneVerification verification = PhoneVerification.builder()
                .phoneNumber(phoneNumber)
                .code(code)
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();

        phoneVerificationWriter.create(verification);

        // TODO: 실제 SMS 연동
        log.info("[SMS MOCK] {} → {}", phoneNumber, code);
    }

    @Transactional
    public void verifyCode(String phoneNumber, String code) {
        PhoneVerification verification =
                phoneVerificationReader.findTopByPhoneNumberOrderByIdDesc(phoneNumber)
                        .orElseThrow(() -> new IllegalArgumentException("인증 요청 없음"));

        if (verification.isVerified())
            throw new IllegalStateException("이미 인증됨");

        if (verification.getExpiredAt().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("인증번호 만료");

        if (!verification.getCode().equals(code))
            throw new IllegalArgumentException("인증번호 불일치");

        verification.verify();
    }

    public void checkVerified(String phoneNumber) {
        if (!phoneVerificationReader.existsByPhoneNumberAndVerifiedTrue(phoneNumber)) {
            throw new IllegalArgumentException("전화번호 인증이 필요합니다.");
        }
    }

    /**
     * 내부 비즈니스 로직 전용 메소드
     *
     * <p><b>주의:</b> Controller에서 직접 호출하지 말 것</p>
     * PhoneVerificationService 와 같은 Service 계층에서만 사용된다.
     */
    private String generateCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }
}
