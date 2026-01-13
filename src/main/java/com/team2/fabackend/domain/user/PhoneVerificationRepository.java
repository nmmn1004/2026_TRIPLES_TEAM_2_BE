package com.team2.fabackend.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneVerificationRepository
        extends JpaRepository<PhoneVerification, Long> {

    Optional<PhoneVerification>
    findTopByPhoneNumberOrderByIdDesc(String phoneNumber);

    boolean existsByPhoneNumberAndVerifiedTrue(String phoneNumber);
}
