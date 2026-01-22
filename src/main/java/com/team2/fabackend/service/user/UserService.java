package com.team2.fabackend.service.user;

import com.team2.fabackend.api.user.dto.UserInfoRequest;
import com.team2.fabackend.api.user.dto.UserInfoResponse;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.exception.CustomException;
import com.team2.fabackend.service.auth.AuthVerificationService;
import com.team2.fabackend.service.auth.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserReader userReader;
    private final UserWriter userWriter;

    private final AuthVerificationService authVerificationService;
    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserInfoResponse getUser(Long id) {
        User user = userReader.findById(id);

        return UserInfoResponse.from(user);
    }

    @Transactional(readOnly = true)
    public String verifyCurrentPassword(Long userId, String rawPassword) {
        User user = userReader.findById(userId);

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String confirmToken = UUID.randomUUID().toString();
        authVerificationService.saveVerificationToken(userId, confirmToken);

        return confirmToken;
    }

    @Transactional
    public void updatePassword(Long userId, String confirmToken, String newPassword) {
        authVerificationService.validateVerificationToken(userId, confirmToken);

        User user = userReader.findById(userId);
        String encodedPassword = passwordEncoder.encode(newPassword);

        userWriter.updatePassword(user, encodedPassword);

        authVerificationService.deleteVerification(userId);
    }

    @Transactional
    public void updateProfile(Long userId, String passwordConfirmToken, UserInfoRequest request) {
        authVerificationService.validateVerificationToken(userId, passwordConfirmToken);

        User user = userReader.findById(userId);

        userWriter.updateProfile(user, request.getName(), request.getNickName(), request.getBirth());

        authVerificationService.deleteVerification(userId);
    }

    @Transactional
    public void deleteUser(Long userId, String passwordConfirmToken) {
        authVerificationService.validateVerificationToken(userId, passwordConfirmToken);

        User user = userReader.findById(userId);
        userWriter.delete(user);

        authVerificationService.deleteVerification(userId);
        refreshTokenService.deleteRefreshToken(userId);
    }
}
