package com.team2.fabackend.domain.user;

import com.team2.fabackend.global.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndSocialType(String email, SocialType socialType);
    boolean existsByEmailAndSocialType(String email, SocialType socialType);
    boolean existsByPhoneNumber(String phoneNumber);
}
