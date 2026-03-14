package com.team2.fabackend.api.auth;

import com.team2.fabackend.api.auth.dto.FindIdResponse;
import com.team2.fabackend.api.auth.dto.LoginRequest;
import com.team2.fabackend.api.auth.dto.LoginResponse;
import com.team2.fabackend.api.auth.dto.PasswordResetRequest;
import com.team2.fabackend.api.auth.dto.RefreshRequest;
import com.team2.fabackend.api.auth.dto.SignupRequest;
import com.team2.fabackend.api.auth.dto.TokenPair;
import com.team2.fabackend.api.phone.dto.PhoneSendRequest;
import com.team2.fabackend.api.phone.dto.PhoneVerifyRequest;
import com.team2.fabackend.service.phoneVerification.PhoneVerificationService;
import com.team2.fabackend.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
     * 사용자의 회원가입 요청을 처리합니다.
     * 전화번호 인증이 선행되어야 하며, 입력 데이터의 유효성을 검증합니다.
     * 
     * @param request 회원가입 정보 (아이디, 비밀번호, 이름 등)
     * @return 성공 시 200 OK
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 전화번호를 이용해 회원가입을 진행합니다. **먼저 /signup/send-code를 통한 번호 인증이 완료되어야 합니다.**")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 누락 등)"),
            @ApiResponse(responseCode = "403", description = "전화번호 인증 미완료")
    })
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 입력된 아이디의 사용 가능 여부를 확인합니다.
     * 중복되지 않은 경우 성공 응답을 반환합니다.
     * 
     * @param userId 중복 체크할 아이디
     * @return 성공 시 200 OK (캐시 제어 헤더 포함)
     */
    @GetMapping("/check-id")
    @Operation(summary = "아이디 중복 체크", description = "입력한 아이디가 이미 가입되어 있는지 확인합니다. (사용 가능 시 200 OK)")
    public ResponseEntity<Void> checkId(@RequestParam String userId) {
        authService.checkUserIdDuplication(userId);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                .build();
    }

    /**
     * 사용자의 아이디와 비밀번호를 확인하여 로그인을 처리합니다.
     * 성공 시 Access Token은 헤더에, Refresh Token은 바디에 담아 반환합니다.
     * 
     * @param request 로그인 정보 (아이디, 비밀번호)
     * @return Refresh Token을 포함한 응답 바디 및 Access Token을 포함한 헤더
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다. AccessToken은 Header(Authorization)로, RefreshToken은 Body로 반환됩니다.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        TokenPair tokens = authService.login(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getAccessToken())
                .body(new LoginResponse(tokens.getRefreshToken()));
    }

    /**
     * 만료된 Access Token을 Refresh Token을 사용하여 재발급합니다.
     * 
     * @param request Refresh Token이 포함된 요청
     * @return 새로운 Access Token(헤더) 및 Refresh Token(바디)
     */
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "만료된 AccessToken을 RefreshToken을 이용해 재발급받습니다.")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest request) {
        TokenPair tokenPair = authService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenPair.getAccessToken())
                .body(new LoginResponse(tokenPair.getRefreshToken()));
    }

    /**
     * 사용자의 로그아웃을 처리하여 서버 측 세션 정보를 삭제합니다.
     * 
     * @param userId 로그아웃할 사용자의 식별자
     * @return 성공 시 200 OK
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "서버 측 세션(Redis 등)에서 리프레시 토큰을 제거하여 로그아웃 처리합니다.")
    public ResponseEntity<Void> logout(@RequestParam Long userId) {
        authService.logout(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 아이디/비밀번호 찾기를 위한 인증번호를 발송합니다.
     * 가입된 전화번호인 경우에만 발송됩니다.
     * 
     * @param request 대상 전화번호 정보
     * @return 성공 시 200 OK
     */
    @PostMapping("/find/send-code")
    @Operation(summary = "[계정찾기용] 인증번호 발송", description = "ID/PW 찾기 전용입니다. **가입되지 않은 번호라면 404 에러**를 반환합니다.")
    public ResponseEntity<Void> sendFindCode(@RequestBody PhoneSendRequest request) {
        phoneVerificationService.sendCodeForFinding(request.getPhoneNumber());
        return ResponseEntity.ok().build();
    }

    /**
     * 인증이 완료된 전화번호를 통해 사용자의 아이디를 조회합니다.
     * 보안을 위해 아이디의 일부는 마스킹 처리되어 반환됩니다.
     * 
     * @param request 대상 전화번호 정보
     * @return 마스킹된 아이디 응답
     */
    @PostMapping("/find/id")
    @Operation(summary = "아이디 찾기", description = "인증 완료된 번호를 통해 마스킹된 아이디를 반환합니다.")
    public ResponseEntity<FindIdResponse> findId(@RequestBody PhoneSendRequest request) {
        String maskedId = authService.findUserId(request.getPhoneNumber());
        return ResponseEntity.ok(new FindIdResponse(maskedId));
    }

    /**
     * 사용자의 비밀번호를 새로운 값으로 재설정합니다.
     * 전화번호 인증이 선행되어야 합니다.
     * 
     * @param request 아이디, 전화번호, 새 비밀번호 정보
     * @return 성공 시 204 No Content
     */
    @PatchMapping("/find/password")
    @Operation(summary = "비밀번호 재설정", description = "아이디와 인증된 번호를 확인한 뒤 비밀번호를 새값으로 변경합니다.")
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 회원가입을 위한 인증번호를 발송합니다.
     * 이미 가입된 전화번호인 경우 에러를 반환합니다.
     * 
     * @param request 대상 전화번호 정보
     * @return 성공 시 200 OK
     */
    @PostMapping("/signup/send-code")
    @Operation(summary = "[회원가입용] 인증번호 발송", description = "회원가입 전용입니다. **이미 가입된 번호라면 409 에러**를 반환합니다.")
    public ResponseEntity<Void> sendSignUpCode(@RequestBody PhoneSendRequest request) {
        phoneVerificationService.sendCodeForSignUp(request.getPhoneNumber());
        return ResponseEntity.ok().build();
    }

    /**
     * 사용자가 입력한 인증번호를 검증합니다.
     * 검증 성공 시 해당 전화번호는 일정 시간 동안 인증된 상태로 유지됩니다.
     * 
     * @param request 전화번호와 인증번호 정보
     * @return 성공 시 200 OK
     */
    @PostMapping("/phone/verify")
    @Operation(summary = "전화번호 인증번호 확인", description = "발송된 6자리 코드를 검증합니다. 성공 시 해당 번호는 15분간 '인증됨' 상태가 유지됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 인증번호 또는 만료된 번호")
    })
    public ResponseEntity<Void> verifyPhoneCode(@RequestBody PhoneVerifyRequest request) {
        phoneVerificationService.verifyCode(request.getPhoneNumber(), request.getCode());
        return ResponseEntity.ok().build();
    }
}
