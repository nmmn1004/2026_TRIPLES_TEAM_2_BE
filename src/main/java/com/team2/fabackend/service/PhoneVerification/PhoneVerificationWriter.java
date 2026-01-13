package com.team2.fabackend.service.PhoneVerification;

import com.team2.fabackend.domain.user.PhoneVerification;
import com.team2.fabackend.domain.user.PhoneVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhoneVerificationWriter {
    private final PhoneVerificationRepository phoneVerificationRepository;

    public void create(PhoneVerification verification) {
        phoneVerificationRepository.save(verification);
    }
}
