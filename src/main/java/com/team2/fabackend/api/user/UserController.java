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

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
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

    /**
     * Retrieves the information of the currently authenticated user.
     *
     * @param userId The ID of the authenticated user.
     * @return A ResponseEntity containing the user's information.
     */
    @GetMapping("/me")
    @Operation(summary = "자신 회원 정보 조회", description = "AccessToken으로 사용자 조회")
    public ResponseEntity<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    /**
     * Retrieves the public profile information of another user.
     *
     * @param userId The ID of the user to retrieve.
     * @return A ResponseEntity containing the user's information.
     */
    @GetMapping("/{userId}")
    @Operation(summary = "타인 회원 정보 조회", description = "공개 프로필 정보를 조회합니다.")
    public ResponseEntity<UserInfoResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    /**
     * Retrieves a paginated list of all users.
     *
     * @param pageable Pagination and sorting information.
     * @return A ResponseEntity containing a page of user information.
     */
    @GetMapping
    @Operation(summary = "전체 유저 페이징 조회")
    public ResponseEntity<Page<UserInfoResponse>> getAllUsers(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    /**
     * Verifies the user's current password and issues a short-lived confirmation token in the header.
     *
     * @param userId  The ID of the authenticated user.
     * @param request The password verification request details.
     * @return A ResponseEntity with the confirmation token in the "X-Password-Confirm-Token" header.
     */
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

    /**
     * Updates the user's profile information. Requires a valid password confirmation token.
     *
     * @param userId               The ID of the authenticated user.
     * @param passwordConfirmToken The confirmation token received from password verification.
     * @param request              The updated user information.
     * @return A ResponseEntity with OK status upon success.
     */
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

    /**
     * Changes the user's password. Requires a valid password confirmation token.
     *
     * @param userId               The ID of the authenticated user.
     * @param passwordConfirmToken The confirmation token received from password verification.
     * @param request              The request containing the new password.
     * @return A ResponseEntity with No Content status upon success.
     */
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

    /**
     * Deletes the authenticated user's account. Requires a valid password confirmation token and a reason.
     *
     * @param userId               The ID of the authenticated user.
     * @param passwordConfirmToken The confirmation token received from password verification.
     * @param request              The user deletion request containing the reason.
     * @return A ResponseEntity with OK status upon success.
     */
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
