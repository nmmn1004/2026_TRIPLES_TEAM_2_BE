package com.team2.fabackend.api.user;

import com.team2.fabackend.api.user.dto.PasswordRequest;
import com.team2.fabackend.api.user.dto.UserDeleteRequest;
import com.team2.fabackend.api.user.dto.UserInfoRequest;
import com.team2.fabackend.api.user.dto.UserInfoResponse;
import com.team2.fabackend.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT") // 클래스 수준 글로벌 보안 설정
@Tag(name = "User", description = """
    ## 유저 관리 API
    사용자 정보 조회, 수정, 비밀번호 변경 및 탈퇴를 제공합니다.
    
    ### 💡 AOS (Kotlin) 요청 가이드
    모든 수정/탈퇴 작업은 **비밀번호 인증(Step 1)** 후 **응답 헤더**로 발급받은 토큰을 사용해야 합니다.
    
    #### 1. Step 1: 비밀번호 인증 및 토큰 추출
    인증 성공 시 토큰은 Body가 아닌 **Response Header**에 담겨 있습니다.
    ```kotlin
    interface UserService {
        @POST("/users/me/password/verify")
        fun verifyPassword(@Body request: PasswordVerifyRequest): Call<Response<Void>>
    }
    
    // 호출 및 헤더 추출 예시
    val response = userService.verifyPassword(request).execute()
    val confirmToken = response.headers()["X-Password-Confirm-Token"]
    ```
    
    #### 2. Step 2: 획득한 토큰으로 정보 수정/탈퇴
    추출한 토큰을 다시 요청 헤더(`X-Password-Confirm-Token`)에 담아 보냅니다.
    ```kotlin
    interface UserService {
        @PATCH("/users/me")
        fun updateProfile(
            @Header("X-Password-Confirm-Token") token: String,
            @Body request: UserInfoRequest
        ): Call<Void>
    }
    ```
    """)
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "자신 회원 정보 조회", description = "AccessToken으로 사용자 조회")
    public ResponseEntity<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal Long userId) {
        log.info("Current User ID: {}", userId);
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "타인 회원 정보 조회", description = "공개 프로필 정보를 조회합니다.")
    public ResponseEntity<UserInfoResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @GetMapping
    @Operation(summary = "전체 유저 페이징 조회")
    public ResponseEntity<Page<UserInfoResponse>> getAllUsers(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PostMapping("/me/password/verify")
    @Operation(summary = "비밀번호 확인", description = "응답 헤더(X-Password-Confirm-Token)로 인증 토큰을 발급합니다.")
    public ResponseEntity<Void> verify(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PasswordRequest.Verify request
    ) {
        String token = userService.verifyCurrentPassword(userId, request.currentPassword());
        return ResponseEntity.ok()
                .header("X-Password-Confirm-Token", token)
                .build();
    }

    @PatchMapping("/me")
    @Operation(
            summary = "회원 정보 수정",
            description = "2차 인증 토큰(Confirm Token)이 필요합니다.",
            security = { @SecurityRequirement(name = "JWT"), @SecurityRequirement(name = "Confirm Token") }
    )
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal Long userId,
            @RequestHeader("X-Password-Confirm-Token") String passwordConfirmToken,
            @Valid @RequestBody UserInfoRequest request
    ) {
        userService.updateProfile(userId, passwordConfirmToken, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/me/password")
    @Operation(
            summary = "비밀번호 변경",
            description = "발급받은 2차 인증 토큰을 사용합니다.",
            security = { @SecurityRequirement(name = "JWT"), @SecurityRequirement(name = "Confirm Token") }
    )
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal Long userId,
            @RequestHeader("X-Password-Confirm-Token") String passwordConfirmToken,
            @Valid @RequestBody PasswordRequest.Update request
    ) {
        userService.updatePassword(userId, passwordConfirmToken, request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    @Operation(
            summary = "회원 탈퇴",
            description = "2차 인증 토큰을 사용하여 회원 정보를 삭제합니다.",
            security = { @SecurityRequirement(name = "JWT"), @SecurityRequirement(name = "Confirm Token") }
    )
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal Long userId,
            @RequestHeader("X-Password-Confirm-Token") String passwordConfirmToken,
            @Valid @RequestBody UserDeleteRequest request
    ) {
        userService.deleteUser(userId, passwordConfirmToken, request);
        return ResponseEntity.ok().build();
    }
}
