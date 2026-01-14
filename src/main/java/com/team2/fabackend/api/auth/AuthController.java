package com.team2.fabackend.api.auth;

import com.team2.fabackend.api.auth.dto.LoginRequest;
import com.team2.fabackend.api.auth.dto.LoginResponse;
import com.team2.fabackend.api.auth.dto.RefreshRequest;
import com.team2.fabackend.api.auth.dto.SignupRequest;
import com.team2.fabackend.api.auth.dto.TokenPair;
import com.team2.fabackend.api.phone.dto.PhoneSendRequest;
import com.team2.fabackend.api.phone.dto.PhoneVerifyRequest;
import com.team2.fabackend.service.PhoneVerification.PhoneVerificationService;
import com.team2.fabackend.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {
    private final AuthService authService;
    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "이메일/비밀번호/전화번호 회원가입, 이전에 전화번호 인증이 필요")
    public ResponseEntity<Void> signup(
            @RequestBody @Valid SignupRequest request
    ) {
        authService.signup(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일/비밀번호 로그인")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {
        TokenPair tokens = authService.login(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getAccessToken())
                .body(new LoginResponse(tokens.getRefreshToken()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "갱신 토큰으로 접근 시 접근 토큰 반환")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest request) {
        String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
                .build();
    }


    @PostMapping("/phone/send")
    @Operation(summary = "전화번호 인증번호 발송", description = "전화번호로 6자리 인증번호 발송")
    public ResponseEntity<Void> sendPhoneCode(
            @RequestBody PhoneSendRequest request) {

        phoneVerificationService.sendCode(request.getPhoneNumber());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/phone/verify")
    @Operation(summary = "전화번호 인증번호 확인", description = "인증 번호와 전화번호로 요청 시 해당 번호가 인증된 것으로 간주")
    public ResponseEntity<Void> verifyPhoneCode(
            @RequestBody PhoneVerifyRequest request) {

        phoneVerificationService.verifyCode(
                request.getPhoneNumber(), request.getCode());
        return ResponseEntity.ok().build();
    }

}
