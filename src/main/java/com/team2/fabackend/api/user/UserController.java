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
@Tag(
        name = "User",
        description = """
    ## 👤 사용자 관리(User) API
    
    프로필 조회, 수정 및 보안 인증을 통한 비밀번호 변경, 탈퇴 기능을 제공합니다.
    
    ---
    
    ### 🔑 주요 특징
    - **보안 인증**: 프로필 수정/탈퇴 시 `Confirm Token`을 통한 2차 검증 단계가 포함됩니다.
    - **유연한 조회**: 본인 프로필뿐만 아니라 타 사용자의 공개 프로필 조회도 가능합니다.
    
    ### 🧩 Flutter / Retrofit 예시
    ```dart
    @RestApi(baseUrl: "https://api.com/users")
    abstract class UserApi {
      @GET("/me")
      Future<UserInfoResponse> getMyProfile();
      
      @PATCH("/me")
      Future<void> updateProfile(@Header("X-Password-Confirm-Token") String token, @Body UserInfoRequest request);
    }
    ```
    """
)
public class UserController {
    private final UserService userService;

    /**
     * 현재 인증된 사용자의 정보를 조회합니다.
     *
     * @param userId 인증된 사용자의 ID
     * @return 사용자의 정보를 포함한 ResponseEntity
     */
    @GetMapping("/me")
    @Operation(
            summary = "내 프로필 정보 조회",
            description = "현재 로그인된 사용자의 닉네임, 이메일, 생년월일 등 상세 정보를 조회합니다."
    )
    public ResponseEntity<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    /**
     * 다른 사용자의 공개 프로필 정보를 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 사용자의 정보를 포함한 ResponseEntity
     */
    @GetMapping("/{userId}")
    @Operation(
            summary = "타 사용자 프로필 조회",
            description = "특정 사용자의 공개된 프로필 정보를 조회합니다."
    )
    public ResponseEntity<UserInfoResponse> getUser(@PathVariable @Parameter(description = "대상 유저 ID", example = "2") Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    /**
     * 전체 사용자 목록을 페이징하여 조회합니다.
     *
     * @param pageable 페이징 및 정렬 정보
     * @return 사용자 정보 페이지를 포함한 ResponseEntity
     */
    @GetMapping
    @Operation(
            summary = "전체 사용자 목록 조회 (관리자용)",
            description = "전체 사용자 리스트를 페이징하여 조회합니다. (기본 10개씩 역순)"
    )
    public ResponseEntity<Page<UserInfoResponse>> getAllUsers(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    /**
     * 사용자의 현재 비밀번호를 검증하고 헤더에 짧은 수명의 확인 토큰을 발급합니다.
     *
     * @param userId  인증된 사용자의 ID
     * @param request 비밀번호 검증 요청 상세 정보
     * @return "X-Password-Confirm-Token" 헤더에 확인 토큰을 포함한 ResponseEntity
     */
    @PostMapping("/me/password/verify")
    @Operation(
            summary = "비밀번호 검증 (보안 인증)",
            description = "정보 수정 전 본인 확인을 위해 비밀번호를 검증합니다. 성공 시 헤더(X-Password-Confirm-Token)로 토큰이 발급됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검증 성공 (헤더에서 토큰 추출 필요)"),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치")
    })
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
     * 사용자의 프로필 정보를 수정합니다. 유효한 비밀번호 확인 토큰이 필요합니다.
     *
     * @param userId               인증된 사용자의 ID
     * @param passwordConfirmToken 비밀번호 검증으로 발급받은 확인 토큰
     * @param request              수정된 사용자 정보
     * @return 성공 시 200 OK 상태의 ResponseEntity
     */
    @PatchMapping("/me")
    @Operation(
            summary = "내 프로필 정보 수정",
            description = "닉네임, 생년월일 등을 수정합니다. 반드시 비밀번호 검증 후 발급받은 'Confirm Token'이 헤더에 포함되어야 합니다."
    )
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal Long userId,
            @RequestHeader("X-Password-Confirm-Token") @Parameter(description = "비밀번호 검증 후 받은 토큰") String passwordConfirmToken,
            @Valid @RequestBody UserInfoRequest request
    ) {
        userService.updateProfile(userId, passwordConfirmToken, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 사용자의 비밀번호를 변경합니다. 유효한 비밀번호 확인 토큰이 필요합니다.
     *
     * @param userId               인증된 사용자의 ID
     * @param passwordConfirmToken 비밀번호 검증으로 발급받은 확인 토큰
     * @param request              새로운 비밀번호를 포함한 요청 객체
     * @return 성공 시 204 No Content 상태의 ResponseEntity
     */
    @PatchMapping("/me/password")
    @Operation(
            summary = "비밀번호 변경",
            description = "새로운 비밀번호로 변경합니다. 'Confirm Token' 헤더가 필요합니다."
    )
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal Long userId,
            @RequestHeader("X-Password-Confirm-Token") @Parameter(description = "비밀번호 검증 후 받은 토큰") String passwordConfirmToken,
            @Valid @RequestBody PasswordRequest.Update request
    ) {
        userService.updatePassword(userId, passwordConfirmToken, request.newPassword());
        return ResponseEntity.noContent().build();
    }

    /**
     * 인증된 사용자의 계정을 삭제합니다. 유효한 비밀번호 확인 토큰과 사유가 필요합니다.
     *
     * @param userId               인증된 사용자의 ID
     * @param passwordConfirmToken 비밀번호 검증으로 발급받은 확인 토큰
     * @param request              탈퇴 사유를 포함한 사용자 탈퇴 요청 객체
     * @return 성공 시 200 OK 상태의 ResponseEntity
     */
    @DeleteMapping("/me")
    @Operation(
            summary = "회원 탈퇴",
            description = "계정을 삭제하고 탈퇴 처리를 진행합니다. 'Confirm Token' 헤더가 필요합니다."
    )
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal Long userId,
            @RequestHeader("X-Password-Confirm-Token") @Parameter(description = "비밀번호 검증 후 받은 토큰") String passwordConfirmToken,
            @Valid @RequestBody UserDeleteRequest request
    ) {
        userService.deleteUser(userId, passwordConfirmToken, request);
        return ResponseEntity.ok().build();
    }
}
