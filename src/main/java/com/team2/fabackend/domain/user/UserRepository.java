package com.team2.fabackend.domain.user;

import com.team2.fabackend.global.enums.SocialType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserIdAndSocialType(String userId, SocialType socialType);
    @NotNull Page<User> findAll(@NotNull Pageable pageable);
    boolean existsByUserIdAndSocialType(String userId, SocialType socialType);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByPhoneNumberAndSocialType(String phoneNumber, SocialType socialType);
    boolean existsByPhoneNumberAndSocialType(String phoneNumber, SocialType socialType);
    Optional<User> findByUserIdAndPhoneNumberAndSocialType(String userId, String phoneNumber, SocialType socialType);
}
