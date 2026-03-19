package com.team2.fabackend.api.auth;

import com.team2.fabackend.api.auth.dto.LoginRequest;
import com.team2.fabackend.api.auth.dto.LoginResponse;
import com.team2.fabackend.api.auth.dto.RefreshRequest;
import com.team2.fabackend.api.auth.dto.SignupRequest;
import com.team2.fabackend.api.auth.dto.TokenPair;
import com.team2.fabackend.api.email.dto.EmailSendRequest;
import com.team2.fabackend.api.email.dto.EmailVerifyRequest;
import com.team2.fabackend.service.phoneVerification.EmailVerificationService;
import com.team2.fabackend.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(
        name = "Auth",
        description = """
    ## 🔐 인증 및 계정 관리(Auth) API
    
    회원가입, 로그인, 토큰 갱신 및 비밀번호 찾기 기능을 제공합니다.
    
    ---
    
    ### 🔑 주요 특징
    - **인증 방식**: Bearer JWT 토큰을 사용하며, Access Token은 헤더로, Refresh Token은 바디로 관리합니다.
    - **이메일 인증**: 모든 가입 및 비밀번호 찾기 시나리오에 이메일 기반 OTP 인증이 포함됩니다.
    
    ### 🧩 Flutter / Retrofit 예시
    ```dart
    @RestApi(baseUrl: "https://api.com/auth")
    abstract class AuthApi {
      @POST("/login")
      Future<HttpResponse<LoginResponse>> login(@Body LoginRequest request);
      
      @POST("/refresh")
      Future<HttpResponse<LoginResponse>> refresh(@Body RefreshRequest request);
    }
    ```
    """
)
public class AuthController {
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    /**
     * 사용자의 회원가입 요청을 처리합니다.
     * 이메일 인증이 선행되어야 하며, 입력 데이터의 유효성을 검증합니다.
     * 
     * @param request 회원가입 정보 (이메일, 비밀번호, 닉네임 등)
     * @return 성공 시 200 OK
     */
    @PostMapping("/signup")
    @Operation(
            summary = "신규 회원가입",
            description = "이메일, 비밀번호, 닉네임 등을 이용해 가입합니다. 호출 전 반드시 이메일 인증(/auth/email/verify)이 완료되어야 합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 검증 실패"),
            @ApiResponse(responseCode = "403", description = "이메일 인증 미완료"),
            @ApiResponse(responseCode = "409", description = "이미 가입된 이메일")
    })
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 입력된 이메일의 사용 가능 여부를 확인합니다.
     * 중복되지 않은 경우 성공 응답을 반환합니다.
     * 
     * @param email 중복 체크할 이메일
     * @return 성공 시 200 OK (캐시 제어 헤더 포함)
     */
    @GetMapping("/check-email")
    @Operation(
            summary = "이메일 중복 확인",
            description = "입력한 이메일이 사용 가능한지 확인합니다. 회원가입 폼에서 포커스를 잃을 때 실시간으로 호출하기 좋습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용 가능한 이메일"),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일")
    })
    public ResponseEntity<Void> checkEmail(@RequestParam @Parameter(description = "중복 확인할 이메일", example = "user@example.com") String email) {
        authService.checkEmailDuplication(email);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                .build();
    }

    /**
     * 사용자의 이메일과 비밀번호를 확인하여 로그인을 처리합니다.
     * 성공 시 Access Token은 헤더에, Refresh Token은 바디에 담아 반환합니다.
     * 
     * @param request 로그인 정보 (이메일, 비밀번호)
     * @return Refresh Token을 포함한 응답 바디 및 Access Token을 포함한 헤더
     */
    @PostMapping("/login")
    @Operation(
            summary = "로그인 (토큰 발급)",
            description = "성공 시 Access Token은 'Authorization' 헤더(Bearer)로, Refresh Token은 Body로 전달됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "ID/PW 불일치")
    })
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
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
    @Operation(
            summary = "토큰 갱신 (Refresh)",
            description = "Access Token이 만료된 경우 호출합니다. 새로운 Access Token은 헤더로, Refresh Token은 Body로 반환됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "Refresh Token 만료 또는 유효하지 않음")
    })
    public ResponseEntity<LoginResponse> refresh(@RequestBody @Valid RefreshRequest request) {
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
    @Operation(
            summary = "로그아웃",
            description = "서버 측 Refresh Token을 무효화합니다. 앱 내 저장된 모든 토큰을 삭제하고 로그인 화면으로 이동하세요."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<Void> logout(@RequestParam @Parameter(description = "유저 ID", example = "1") Long userId) {
        authService.logout(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 찾기를 위한 인증번호를 발송합니다.
     * 가입된 이메일인 경우에만 발송됩니다.
     * 
     * @param request 대상 이메일 정보
     * @return 성공 시 200 OK
     */
    @PostMapping("/find/send-code")
    @Operation(
            summary = "비밀번호 찾기 - 인증코드 발송",
            description = "비밀번호 재설정을 위해 가입된 이메일로 6자리 인증 코드를 보냅니다."
    )
    public ResponseEntity<Void> sendFindCode(@RequestBody @Valid EmailSendRequest request) {
        emailVerificationService.sendCodeForFinding(request.getEmail());
        return ResponseEntity.ok().build();
    }

    /**
     * 이메일 인증 후 임시 비밀번호를 발송합니다.
     * 
     * @param request 이메일 및 인증번호 정보
     * @return 성공 시 200 OK
     */
    @PostMapping("/find/password")
    @Operation(
            summary = "비밀번호 찾기 - 임시 비밀번호 발급",
            description = "인증 코드 검증 후, 해당 이메일로 임시 비밀번호를 전송합니다."
    )
    public ResponseEntity<Void> sendTemporaryPassword(@RequestBody @Valid EmailVerifyRequest request) {
        authService.sendTemporaryPassword(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 회원가입을 위한 인증번호를 발송합니다.
     * 이미 가입된 이메일인 경우 에러를 반환합니다.
     * 
     * @param request 대상 이메일 정보
     * @return 성공 시 200 OK
     */
    @PostMapping("/signup/send-code")
    @Operation(
            summary = "회원가입 - 인증코드 발송",
            description = "신규 가입을 위해 입력한 이메일로 6자리 인증 코드를 보냅니다. 중복 이메일은 409를 반환합니다."
    )
    public ResponseEntity<Void> sendSignUpCode(@RequestBody @Valid EmailSendRequest request) {
        emailVerificationService.sendCodeForSignUp(request.getEmail());
        return ResponseEntity.ok().build();
    }

    /**
     * 사용자가 입력한 인증번호를 검증합니다.
     * 검증 성공 시 해당 이메일은 일정 시간 동안 인증된 상태로 유지됩니다.
     * 
     * @param request 이메일과 인증번호 정보
     * @return 성공 시 200 OK
     */
    @PostMapping("/email/verify")
    @Operation(
            summary = "이메일 인증 코드 검증",
            description = "사용자가 입력한 6자리 코드가 일치하는지 확인합니다. 성공 시 가입 처리가 가능해집니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "400", description = "코드 불일치 또는 만료")
    })
    public ResponseEntity<Void> verifyEmailCode(@RequestBody @Valid EmailVerifyRequest request) {
        emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok().build();
    }
}
