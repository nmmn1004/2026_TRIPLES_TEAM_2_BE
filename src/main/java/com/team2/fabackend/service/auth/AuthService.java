package com.team2.fabackend.service.auth;

import com.team2.fabackend.api.auth.dto.LoginRequest;
import com.team2.fabackend.api.auth.dto.SignupRequest;
import com.team2.fabackend.api.auth.dto.TokenPair;
import com.team2.fabackend.api.email.dto.EmailVerifyRequest;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.global.enums.ErrorCode;
import com.team2.fabackend.global.enums.SocialType;
import com.team2.fabackend.global.exception.CustomException;
import com.team2.fabackend.global.security.JwtProvider;
import com.team2.fabackend.service.mail.MailService;
import com.team2.fabackend.service.phoneVerification.EmailVerificationService;
import com.team2.fabackend.service.user.UserReader;
import com.team2.fabackend.service.user.UserWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserReader userReader;
    private final UserWriter userWriter;
    private final EmailVerificationService emailVerificationService;
    private final MailService mailService;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    private final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);

    /**
     * 이메일 인증 확인 후 신규 사용자를 등록합니다.
     * 중복된 이메일이 있는지 사전에 검증합니다.
     * 
     * @param request 회원가입 요청 정보 (이메일, 비밀번호, 닉네임, 생년월일 등)
     */
    @Transactional
    public void signup(SignupRequest request) {
        emailVerificationService.checkVerified(request.getEmail());

        if (userReader.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_USER_ID);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .socialType(SocialType.LOCAL)
                .nickName(request.getNickName())
                .birth(request.getBirth())
                .build();

        userWriter.create(user);
        emailVerificationService.clearVerificationLog(request.getEmail());
    }

    /**
     * 입력된 이메일의 중복 여부를 확인합니다.
     * 
     * @param email 중복 체크할 이메일
     */
    @Transactional(readOnly = true)
    public void checkEmailDuplication(String email) {
        if (userReader.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_USER_ID);
        }
    }

    /**
     * 사용자의 이메일과 비밀번호를 검증하여 로그인을 처리하고 토큰 쌍을 발급합니다.
     * 
     * @param request 로그인 요청 정보 (이메일, 비밀번호)
     * @return 발급된 Access Token과 Refresh Token 쌍
     */
    @Transactional
    public TokenPair login(LoginRequest request) {
        User user = userReader.findByEmailAndSocialType(request.getEmail(), SocialType.LOCAL);

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
     * 이메일 인증 확인 후 임시 비밀번호를 생성하여 발송합니다.
     * 
     * @param request 이메일 및 인증번호 정보
     */
    @Transactional
    public void sendTemporaryPassword(EmailVerifyRequest request) {
        emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        
        User user = userReader.findByEmailAndSocialType(request.getEmail(), SocialType.LOCAL);
        
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        userWriter.updatePassword(user, passwordEncoder.encode(tempPassword));
        
        mailService.sendMail(user.getEmail(), "[서비스명] 임시 비밀번호 안내", "임시 비밀번호는 [" + tempPassword + "] 입니다. 로그인 후 반드시 비밀번호를 변경해주세요.");
        
        emailVerificationService.clearVerificationLog(request.getEmail());
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
}
