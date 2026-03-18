package com.team2.fabackend.service.user;

import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.domain.user.UserRepository;
import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.enums.SocialType;
import com.team2.fabackend.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReader {
    private final UserRepository userRepository;

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User findByEmailAndSocialType(String email, SocialType socialType) {
        return userRepository.findByEmailAndSocialType(email, socialType)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndSocialType(email, SocialType.LOCAL);
    }

    public boolean existsByDeviceId(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return false;
        }
        return userRepository.existsByDeviceId(deviceId);
    }

    public boolean existsByNickName(String nickName) {
        if (nickName == null || nickName.isBlank()) {
            return false;
        }
        return userRepository.existsByNickName(nickName);
    }
}
