package com.team2.fabackend.service.auth;

import com.team2.fabackend.api.auth.dto.LoginRequest;
import com.team2.fabackend.api.auth.dto.PasswordResetRequest;
import com.team2.fabackend.api.auth.dto.SignupRequest;
import com.team2.fabackend.api.auth.dto.TokenPair;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.enums.SocialType;
import com.team2.fabackend.global.exception.CustomException;
import com.team2.fabackend.global.security.JwtProvider;
import com.team2.fabackend.service.phoneVerification.PhoneVerificationService;
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

    /**
     * 전화번호 인증 확인 후 신규 사용자를 등록합니다.
     * 중복된 아이디나 전화번호가 있는지 사전에 검증합니다.
     * 
     * @param request 회원가입 요청 정보 (아이디, 비밀번호, 닉네임, 생년월일, 전화번호 등)
     */
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
    }

    /**
     * 입력된 아이디의 중복 여부를 확인합니다.
     * 
     * @param userId 중복 체크할 아이디
     */
    @Transactional(readOnly = true)
    public void checkUserIdDuplication(String userId) {
        if (userReader.existsByUserId(userId)) {
            throw new CustomException(ErrorCode.DUPLICATE_USER_ID);
        }
    }

    /**
     * 사용자의 아이디와 비밀번호를 검증하여 로그인을 처리하고 토큰 쌍을 발급합니다.
     * 
     * @param request 로그인 요청 정보 (아이디, 비밀번호)
     * @return 발급된 Access Token과 Refresh Token 쌍
     */
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

    /**
     * 유효한 Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급합니다.
     * 
     * @param refreshToken 현재 사용 중인 Refresh Token
     * @return 새로 발급된 토큰 쌍
     */
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

    /**
     * 인증된 전화번호를 통해 해당 사용자의 마스킹된 아이디를 찾습니다.
     * 
     * @param phoneNumber 인증된 사용자의 전화번호
     * @return 마스킹 처리된 아이디
     */
    public String findUserId(String phoneNumber) {
        phoneVerificationService.checkVerified(phoneNumber);

        User user = userReader.findGeneralUserByPhone(phoneNumber);

        phoneVerificationService.clearVerificationLog(phoneNumber);

        return maskUserId(user.getUserId());
    }

    /**
     * 인증된 전화번호와 아이디를 확인한 후 사용자의 비밀번호를 재설정합니다.
     * 
     * @param request 비밀번호 재설정 요청 정보 (아이디, 전화번호, 새 비밀번호)
     */
    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        phoneVerificationService.checkVerified(request.getPhoneNumber());

        User user = userReader.findGeneralUserByIdAndPhone(request.getUserId(), request.getPhoneNumber());

        userWriter.updatePassword(user, passwordEncoder.encode(request.getNewPassword()));
        phoneVerificationService.clearVerificationLog(request.getPhoneNumber());
    }

    /**
     * 사용자의 Refresh Token을 삭제하여 로그아웃 처리합니다.
     * 
     * @param userId 로그아웃할 사용자의 식별자
     */
    @Transactional
    public void logout(Long userId) {
        refreshTokenService.deleteRefreshToken(userId);
    }

    /**
     * 사용자 아이디의 뒷부분을 마스킹 처리합니다.
     * 
     * @param userId 원본 아이디
     * @return 마스킹된 아이디
     */
    private String maskUserId(String userId) {
        if (userId.length() <= 3) return userId.substring(0, 1) + "**";

        return userId.substring(0, userId.length() - 3) + "***";
    }
}
