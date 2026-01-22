package com.team2.fabackend.service.auth;

import com.team2.fabackend.api.auth.dto.LoginRequest;
import com.team2.fabackend.api.auth.dto.SignupRequest;
import com.team2.fabackend.api.auth.dto.SignupResponse;
import com.team2.fabackend.api.auth.dto.TokenPair;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.enums.SocialType;
import com.team2.fabackend.global.exception.CustomException;
import com.team2.fabackend.global.security.JwtProvider;
import com.team2.fabackend.service.PhoneVerification.PhoneVerificationService;
import com.team2.fabackend.service.user.UserReader;
import com.team2.fabackend.service.user.UserWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserReader userReader;
    private final UserWriter userWriter;
    private final PhoneVerificationService phoneVerificationService;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    private final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);

    @Transactional
    public void signup(SignupRequest request) {
        phoneVerificationService.checkVerified(request.getPhoneNumber());

        if (userReader.existsByUserId(request.getUserId())) {
            throw new CustomException(ErrorCode.DUPLICATE_USER_ID);
        }

        if (userReader.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new CustomException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        User user = User.builder()
                .userId(request.getUserId())
                .password(passwordEncoder.encode(request.getPassword()))
                .socialType(SocialType.LOCAL)
                .name(request.getName())
                .nickName(request.getNickName())
                .birth(request.getBirth())
                .phoneNumber(request.getPhoneNumber())
                .build();

        userWriter.create(user);

        phoneVerificationService.clearVerificationLog(request.getPhoneNumber());

        new SignupResponse(user.getId());
    }

    @Transactional(readOnly = true)
    public void checkUserIdDuplication(String userId) {
        if (userReader.existsByUserId(userId)) {
            throw new CustomException(ErrorCode.DUPLICATE_USER_ID);
        }
    }

    @Transactional
    public TokenPair login(LoginRequest request) {
        User user = userReader.findByUserIdAndSocialType(request.getUserId(), SocialType.LOCAL);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserType());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        refreshTokenService.saveRefreshToken(user.getId(), refreshToken, REFRESH_TOKEN_TTL);

        return new TokenPair(accessToken, refreshToken);
    }

    @Transactional
    public TokenPair refreshAccessToken(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtProvider.getUserIdFromToken(refreshToken);

        try {
            refreshTokenService.validateRefreshToken(userId, refreshToken);
        } catch (CustomException e) {
            refreshTokenService.deleteRefreshToken(userId);
            throw e;
        }

        User user = userReader.findById(userId);

        String newAccessToken = jwtProvider.createAccessToken(user.getId(), user.getUserType());
        String newRefreshToken = jwtProvider.createRefreshToken(user.getId());

        refreshTokenService.saveRefreshToken(user.getId(), newRefreshToken, REFRESH_TOKEN_TTL);

        return new TokenPair(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenService.deleteRefreshToken(userId);
    }
}
