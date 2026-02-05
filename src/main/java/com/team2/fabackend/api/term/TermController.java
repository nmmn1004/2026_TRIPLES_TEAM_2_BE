package com.team2.fabackend.api.term;

import com.team2.fabackend.api.term.dto.AgreedTermRequest;
import com.team2.fabackend.api.term.dto.TermInfoResponse;
import com.team2.fabackend.api.term.dto.UserTermStatusResponse;
import com.team2.fabackend.service.userTerm.UserTermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/terms")
@RequiredArgsConstructor
@Tag(
        name = "Term",
        description = """
        ## 약관 관리 API
        현재 서비스에서 사용 중인 **유효한 약관 조회**,  
        **유저의 약관 동의 현황 조회**,  
        **약관 동의 처리** 기능을 제공합니다.

        ---

        ### 💡 AOS (Kotlin / Retrofit) 요청 가이드

        #### 공통 사항
        - 모든 `/terms/me`, `/terms` (POST) API는 **로그인 필요**
        - `Authorization: Bearer {accessToken}` 헤더 필수
        - 서버는 **현재 유효한 약관(active terms)** 기준으로만 응답합니다.

        ---

        #### 1️⃣ 현재 유효한 약관 목록 조회 (약관 화면 표시용)
        회원가입 또는 로그인 후 약관 동의 화면에서 사용합니다.

        ```kotlin
        interface TermService {
            @GET("/terms/active")
            suspend fun getActiveTerms(): List<TermInfoResponse>
        }
        ```

        - `required = true` → 필수 약관
        - `required = false` → 선택 약관
        - 프론트에서는 `required` 값으로 체크 필수 여부를 판단하세요.

        ---

        #### 2️⃣ 내 약관 동의 현황 조회 (체크 상태 표시)
        로그인 후 **약관 재동의 필요 여부 판단** 또는  
        마이페이지 > 약관 관리 화면에서 사용합니다.

        ```kotlin
        interface TermService {
            @GET("/terms/me")
            suspend fun getMyTermStatus(): List<UserTermStatusResponse>
        }
        ```

        - 서버에서 **약관 + 동의 여부를 조합해서 반환**
        - 프론트는 `agreed` 값만 사용해 체크 상태를 표시하면 됩니다.

        ---

        #### 3️⃣ 약관 동의 처리
        사용자가 약관 동의 버튼을 눌렀을 때 호출합니다.

        ```kotlin
        interface TermService {
            @POST("/terms")
            suspend fun agreeTerms(
                @Body request: AgreedTermRequest
            ): Response<Unit>
        }
        ```

        ```kotlin
        data class AgreedTermRequest(
            val agreedTermIds: List<Long>
        )
        ```

        ⚠️ 주의사항
        - **필수 약관 미동의 시 요청은 실패합니다.**
        - 서버에서 유효성 검증을 수행하므로,
          프론트는 단순히 체크된 약관 ID만 전달하면 됩니다.
        - 이미 동의한 약관 ID를 다시 보내도 무시됩니다.

        ---
        """
)
public class TermController {

    private final UserTermService userTermService;

    /**
     * 현재 유효한 약관 목록 조회
     */
    @Operation(summary = "현재 유효한 약관 목록 조회")
    @GetMapping("/active")
    public ResponseEntity<List<TermInfoResponse>> getActiveTerms() {
        return ResponseEntity.ok(userTermService.getActiveTerms());
    }

    /**
     * 내 약관 동의 현황 조회
     */
    @Operation(summary = "내 약관 동의 현황 조회")
    @GetMapping("/me")
    public ResponseEntity<List<UserTermStatusResponse>> getUserTermStatus(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(userTermService.getUserTermStatus(userId));
    }

    /**
     * 약관 동의 처리
     */
    @Operation(summary = "약관 동의 처리")
    @PostMapping
    public ResponseEntity<Void> agreeTerms(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AgreedTermRequest request
    ) {
        userTermService.agreeTerms(userId, request.getAgreedTermIds());
        return ResponseEntity.ok().build();
    }
}
