package com.team2.fabackend.service.PhoneVerification;

import com.team2.fabackend.domain.user.PhoneVerification;
import com.team2.fabackend.domain.user.PhoneVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhoneVerificationReader {
    private final PhoneVerificationRepository phoneVerificationRepository;

    public Optional<PhoneVerification> findTopByPhoneNumberOrderByIdDesc(String phoneNumber) {
        return phoneVerificationRepository.findTopByPhoneNumberOrderByIdDesc(phoneNumber);
    }

    public boolean existsByPhoneNumberAndVerifiedTrue(String phoneNumber) {
        return phoneVerificationRepository.existsByPhoneNumberAndVerifiedTrue(phoneNumber);
    }
}
