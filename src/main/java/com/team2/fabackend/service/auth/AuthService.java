package com.team2.fabackend.service.auth;

import com.team2.fabackend.api.user.dto.LoginRequest;
import com.team2.fabackend.api.user.dto.LoginResponse;
import com.team2.fabackend.api.user.dto.SignupRequest;
import com.team2.fabackend.api.user.dto.SignupResponse;
import com.team2.fabackend.api.user.dto.TokenPair;
import com.team2.fabackend.domain.user.User;
import com.team2.fabackend.global.enums.SocialType;
import com.team2.fabackend.global.security.JwtProvider;
import com.team2.fabackend.service.PhoneVerification.PhoneVerificationService;
import com.team2.fabackend.service.user.UserReader;
import com.team2.fabackend.service.user.UserWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserReader userReader;
    private final UserWriter userWriter;
    private final PhoneVerificationService phoneVerificationService;

    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        phoneVerificationService.checkVerified(request.getPhoneNumber());

        if (userReader.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        if (userReader.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 가입된 전화번호입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .socialType(SocialType.LOCAL)
                .name(request.getName())
                .nickName(request.getNickName())
                .birthYear(request.getBirthYear())
                .phoneNumber(request.getPhoneNumber())
                .build();

        userWriter.create(user);

        return new SignupResponse(user.getId());
    }

    public TokenPair login(LoginRequest request) {
        User user = userReader.findByEmailAndSocialType(request.getEmail(), SocialType.LOCAL);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다");
        }

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUserType());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        return new TokenPair(accessToken, refreshToken); // Access Token은 Header, Refresh Token은 Body에서 처리
    }

    @Transactional
    public String refreshAccessToken(String refreshToken) {

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰");
        }

        Long userId = jwtProvider.getUserIdFromToken(refreshToken);

        User user = userReader.findById(userId);

        return jwtProvider.createAccessToken(user.getId(), user.getUserType());
    }
}
