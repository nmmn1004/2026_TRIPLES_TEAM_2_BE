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
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = """
    ## 인증 및 회원가입 API
    
    ### 💡 [중요] 실시간 중복 체크 (Debouncing) 가이드
    아이디(`userId`) 입력 시 실시간 중복 체크를 구현할 때는 서버 부하를 줄이기 위해 반드시 **Debouncing**을 적용해야 합니다.
    
    #### 1. Debouncing 이란?
    사용자가 입력을 멈춘 후 특정 시간(예: 300ms) 동안 추가 입력이 없을 때만 API를 호출하는 방식입니다.
    
    #### 2. Kotlin (Coroutine) 구현 예시
    ```kotlin
    // ViewModel 내부 예시
    private var searchJob: Job? = null
    
    fun onUserIdChanged(newId: String) {
        searchJob?.cancel() // 이전 대기 중인 요청 취소
        searchJob = viewModelScope.launch {
            delay(300L) // 300ms 대기
            if (newId.length >= 4) { // 최소 글자수 제한 권장
                checkUserIdDuplication(newId)
            }
        }
    }
    ```
    
    #### 3. 추천 정책
    - **최소 호출 글자수:** 4자 이상부터 요청 권장
    - **지연 시간:** 300ms ~ 500ms
    - **에러 처리:** 중복 시 `409 Conflict (A001)` 에러 응답을 기반으로 UI 처리
    """)
public class AuthController {
    private final AuthService authService;
    private final PhoneVerificationService phoneVerificationService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "이메일/비밀번호/전화번호 회원가입, 이전에 전화번호 인증이 필요")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-id")
    public ResponseEntity<Void> checkId(@RequestParam String userId) {
        authService.checkUserIdDuplication(userId);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                .build();
    }

    /**
     * 로그인
     * AccessToken → Header, RefreshToken → Body
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일/비밀번호 로그인")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        TokenPair tokens = authService.login(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getAccessToken())
                .body(new LoginResponse(tokens.getRefreshToken()));
    }

    /**
     * AccessToken 재발급
     * Redis에서 RefreshToken 검증 후 AccessToken 재발급
     */
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "갱신 토큰으로 접근 시 새로운 접근 토큰 반환")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest request) {
        TokenPair tokenPair = authService.refreshAccessToken(request.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenPair.getAccessToken())
                .body(new LoginResponse(tokenPair.getRefreshToken()));
    }

    /**
     * 로그아웃
     * Redis에서 RefreshToken 삭제
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "Redis에서 RefreshToken 삭제")
    public ResponseEntity<Void> logout(@RequestParam Long userId) {
        authService.logout(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/phone/send")
    @Operation(summary = "전화번호 인증번호 발송", description = "전화번호로 6자리 인증번호 발송")
    public ResponseEntity<Void> sendPhoneCode(@RequestBody PhoneSendRequest request) {
        phoneVerificationService.sendCode(request.getPhoneNumber());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/phone/verify")
    @Operation(summary = "전화번호 인증번호 확인", description = "인증 번호와 전화번호로 요청 시 해당 번호가 인증된 것으로 간주")
    public ResponseEntity<Void> verifyPhoneCode(@RequestBody PhoneVerifyRequest request) {
        phoneVerificationService.verifyCode(request.getPhoneNumber(), request.getCode());
        return ResponseEntity.ok().build();
    }
}
