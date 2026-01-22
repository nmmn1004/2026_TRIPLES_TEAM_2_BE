package com.team2.fabackend.api.user;

import com.team2.fabackend.api.user.dto.PasswordRequest;
import com.team2.fabackend.api.user.dto.UserInfoRequest;
import com.team2.fabackend.api.user.dto.UserInfoResponse;
import com.team2.fabackend.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    val confirmToken = response.headers()["X-Password-Confirm_Token"]
    ```
    
    #### 2. Step 2: 획득한 토큰으로 정보 수정/탈퇴
    추출한 토큰을 다시 요청 헤더(`X-Password-Confirm_Token`)에 담아 보냅니다.
    ```kotlin
    interface UserService {
        @PATCH("/users/me")
        fun updateProfile(
            @Header("X-Password-Confirm_Token") token: String,
            @Body request: UserInfoRequest
        ): Call<Void>
        
        @DELETE("/users/me")
        fun deleteUser(
            @Header("X-Password-Confirm_Token") token: String
        ): Call<Void>
    }
    ```
    
    #### 3. 주의사항
    - **유효 시간:** 인증 토큰은 발급 후 **10분간**만 유효합니다.
    - **보안:** `X-Password-Confirm_Token`은 민감한 권한을 가지므로 반드시 `https` 환경에서 통신하세요.
    - **CORS:** 브라우저 환경에서 테스트 시 해당 헤더가 보이지 않는다면 서버의 `ExposedHeaders` 설정을 확인하세요.
    """)
public class UserController {
    private final UserService userService;

    /**
     * 회원 정보 조회
     */
//    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/me")
    @Operation(summary = "자신 회원 정보 조회", description = "AccessToken으로 사용자 조회")
    public ResponseEntity<UserInfoResponse> getCurrentUser(
            @AuthenticationPrincipal Long userId
    ) {
        UserInfoResponse response = userService.getUser(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "타인 회원 정보 조회",
            description = "로그인한 유저는 특정 사용자의 공개 프로필 정보를 조회할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (AccessToken 누락/만료)"),
            @ApiResponse(description = "유저를 찾을 수 없음", responseCode = "404")
    })
    public ResponseEntity<UserInfoResponse> getUser(
            @PathVariable Long userId
    ) {
        UserInfoResponse response = userService.getUser(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "전체 유저 페이징 조회",
            description = "## AOS/Retrofit 요청 가이드\n" +
                    "- **기본 파라미터**: `page`(0부터 시작), `size`(페이지당 개수), `sort`(필드,방향)\n" +
                    "- **요청 예시**: `baseUrl/users?page=0&size=10&sort=id,desc`\n\n" +
                    "### 응답 구조 안내\n" +
                    "- `content`: 유저 데이터 리스트 (`List<UserInfoResponse>`)\n" +
                    "- `last`: 마지막 페이지 여부 (무한 스크롤 구현 시 사용)\n" +
                    "- `totalElements`: 전체 유저 수\n" +
                    "- `number`: 현재 페이지 번호"
    )
    public ResponseEntity<Page<UserInfoResponse>> getAllUsers(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    /**
     * 비밀번호 인증
     */
    @PostMapping("/me/password/verify")
    @Operation(
            summary = "비밀번호 확인",
            description = "기존 비밀번호를 확인하고 응답 헤더(X-Password-Confirm_Token)로 인증 토큰을 발급합니다."
    )
    public ResponseEntity<Void> verify(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PasswordRequest.Verify request
    ) {
        String token = userService.verifyCurrentPassword(userId, request.currentPassword());

        return ResponseEntity.ok()
                .header("X-Password-Confirm_Token", token)
                .build();
    }

    /**
     * 회원 정보 수정
     */
    @PatchMapping("/me")
    @Operation(summary = "회원 정보 수정", description = "헤더의 토큰으로 본인 인증 후 프로필 수정")
    @Parameter(
            name = "X-Password-Confirm_Token",
            description = "비밀번호 확인 후 발급받은 인증 토큰",
            required = true,
            in = ParameterIn.HEADER
    )
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal Long userId,
            @Parameter(description = "비밀번호 확인 후 발급받은 인증 토큰 (유효시간 10분)")
            @RequestHeader("X-Password-Confirm-Token") String passwordConfirmToken,
            @Valid @RequestBody UserInfoRequest request
    ) {
        userService.updateProfile(userId, passwordConfirmToken, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 비밀번호 변경
     */
    @PatchMapping("/me/password")
    @Operation(summary = "비밀번호 변경", description = "발급받은 헤더의 토큰을 사용하여 비밀번호를 변경합니다.")
    @Parameter(
            name = "X-Password-Confirm_Token",
            description = "비밀번호 확인 후 발급받은 인증 토큰",
            required = true,
            in = ParameterIn.HEADER
    )
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal Long userId,
            @RequestHeader("X-Password-Confirm_Token") String passwordConfirmToken,
            @Valid @RequestBody PasswordRequest.Update request
    ) {
        userService.updatePassword(userId, passwordConfirmToken, request.newPassword());
        return ResponseEntity.noContent().build();
    }

    /**
     * 회원 삭제
     */
    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴", description = "헤더의 토큰으로 본인 인증 후 회원 탈퇴")
    @Parameter(
            name = "X-Password-Confirm_Token",
            description = "비밀번호 확인 후 발급받은 인증 토큰",
            required = true,
            in = ParameterIn.HEADER
    )
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal Long userId,
            @RequestHeader("X-Password-Confirm_Token") String passwordConfirmToken
    ) {
        userService.deleteUser(userId, passwordConfirmToken);
        return ResponseEntity.ok().build();
    }
}
