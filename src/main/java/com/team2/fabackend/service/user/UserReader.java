package com.team2.fabackend.service.user;

import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.domain.user.UserRepository;
import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.enums.SocialType;
import com.team2.fabackend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReader {
    private final UserRepository userRepository;

    public User findByUserIdAndSocialType(String userId, SocialType socialType) {
        return userRepository.findByUserIdAndSocialType(userId, socialType)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.USER_NOT_FOUND)
                );
    }

    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserIdAndSocialType(userId, SocialType.LOCAL);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.USER_NOT_FOUND)
                );
    }
}
