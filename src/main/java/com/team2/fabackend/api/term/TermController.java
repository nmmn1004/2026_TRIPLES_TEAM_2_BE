package com.team2.fabackend.api.term;

import com.team2.fabackend.api.error.dto.ErrorResponse;
import com.team2.fabackend.api.term.dto.AgreedTermRequest;
import com.team2.fabackend.api.term.dto.TermInfoResponse;
import com.team2.fabackend.api.term.dto.TermSaveRequest;
import com.team2.fabackend.api.term.dto.UserTermStatusResponse;
import com.team2.fabackend.service.userTerm.UserTermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/terms")
@RequiredArgsConstructor
@Tag(
        name = "Term",
        description = """
    ## 📜 약관 관리 API  
    서비스 내 **약관 정보 조회 및 동의 처리 기능**을 제공합니다.  
    
    ---
    
    ### ✅ 주요 기능
    1. 현재 서비스에서 사용 중인 **유효한 약관 목록 조회 (/active)**
    2. 로그인 사용자의 **약관 동의 현황 조회 (/me)**
    3. 사용자의 **약관 동의 처리 (/agree)**
    
    ---
    
    ### ⚙️ 공통 요청 조건
    - `/terms/me`, `/terms/agree` API는 **로그인 필요**
    - `Authorization: Bearer {accessToken}` 헤더 필수  
      → Retrofit `@Header("Authorization")` 형태로 추가하세요.
    
    ---
    
    ### 🧩 Retrofit 예시 코드 (AOS)
    ```kotlin
    interface TermService {
        // 1️⃣ 유효한 약관 목록 조회
        @GET("/terms/active")
        suspend fun getActiveTerms(): List<TermInfoResponse>
    
        // 2️⃣ 내 약관 동의 현황 조회
        @GET("/terms/me")
        suspend fun getMyTermStatus(): List<UserTermStatusResponse>
    
        // 3️⃣ 약관 동의 처리
        @POST("/terms/agree")
        suspend fun agreeTerms(
            @Body request: AgreedTermRequest
        ): Response<Unit>
    }
    ```
    
    ---
    ### ⚠️ 유의사항
    - **필수 약관(required = true)** 미동의 시 서버에서 400 에러 반환  
    - 이미 동의한 약관 ID를 재전송해도 무시됩니다  
    - `effectiveAt`은 프론트에서 약관 최신 여부를 판단할 때 사용하세요  
    """
)
public class TermController {
    private final UserTermService userTermService;

    /**
     * 현재 활성화된 약관 목록을 조회합니다.
     *
     * @return 활성화된 약관 목록을 포함한 ResponseEntity
     */
    @GetMapping("/active")
    @Operation(
            summary = "현재 유효한 약관 목록 조회",
            description = """
        현재 서비스에서 활성화된(**유효한**) 약관 목록을 조회합니다.  
        
        ---
        
        ### 🚀 요청 예시
        ```kotlin
        val response = termService.getActiveTerms()
        ```
        
        ### 📦 응답 예시
        ```json
        [
          {
            "id": 1,
            "title": "서비스 이용약관",
            "version": "v2.0",
            "required": true,
            "content": "<p>...</p>",
            "effectiveAt": "2026-02-01"
          }
        ]
        ```
        
        - `required = true` → 필수 동의 항목  
        - `required = false` → 선택 동의 항목
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "현재 유효한 약관 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = TermInfoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 (S001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<TermInfoResponse>> getActiveTerms() {
        return ResponseEntity.ok(userTermService.getActiveTerms());
    }

    /**
     * 현재 인증된 사용자의 약관 동의 현황을 조회합니다.
     *
     * @param userId 인증된 사용자의 ID
     * @return 약관 목록과 동의 현황을 포함한 ResponseEntity
     */
    @GetMapping("/me")
    @Operation(
            summary = "내 약관 동의 현황 조회",
            description = """
        로그인한 사용자의 약관 동의 상태를 조회합니다.  
        
        주로 **마이페이지 약관 관리 화면** 또는  
        **약관 재동의 여부 판단 로직**에서 사용됩니다.
        
        ---
        
        ### 🚀 요청 예시
        ```kotlin
        val response = termService.getMyTermStatus()
        ```
        
        ### 📦 응답 예시
        ```json
        [
          {
            "termId": 1,
            "title": "서비스 이용약관",
            "version": "v2.0",
            "required": true,
            "agreed": true,
            "agreedAt": "2026-02-01T10:00:00"
          }
        ]
        ```
        
        - `agreed = true` → 이미 동의함  
        - `agreedAt` 값은 동의한 일시
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "내 약관 동의 현황 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserTermStatusResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (T 계열 토큰 에러, 로그인 필요)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음 (U001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 (S001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<UserTermStatusResponse>> getUserTermStatus(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(userTermService.getUserTermStatus(userId));
    }

    /**
     * 인증된 사용자의 약관 동의를 처리합니다.
     *
     * @param userId  인증된 사용자의 ID
     * @param request 동의한 약관 ID 목록을 포함하는 요청 객체
     * @return 성공 시 200 OK 상태의 ResponseEntity
     */
    @PostMapping("/agree")
    @Operation(
            summary = "약관 동의 처리",
            description = """
        사용자가 약관 동의 버튼을 눌렀을 때 호출합니다.  
        
        서버에서 유효성 검사(필수 약관 포함)를 수행하며,  
        **이미 동의한 약관은 무시됩니다.**
        
        ---
        
        ### 🚀 요청 예시
        ```kotlin
        val request = AgreedTermRequest(listOf(1, 2, 3))
        termService.agreeTerms(request)
        ```
        
        ### 📦 요청 본문
        ```json
        {
          "agreedTermIds": [1, 2, 3]
        }
        ```
        
        ### ⚠️ 주의
        - 필수 약관을 미포함 시 400 Bad Request  
        - 로그인 필요 (`Authorization` 헤더 필수)
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "약관 동의 처리 성공 (이미 동의한 약관은 무시)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "필수 약관 미동의 또는 유효하지 않은 약관 ID (S002/S003 등)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (T 계열 토큰 에러, 로그인 필요)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음 (U001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 (S001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> agreeTerms(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody AgreedTermRequest request
    ) {
        userTermService.agreeTerms(userId, request.getAgreedTermIds());
        return ResponseEntity.ok().build();
    }

    /**
     * 새로운 약관 레코드를 생성합니다. 이 작업은 관리자 권한이 필요합니다.
     *
     * @param request 저장할 약관 상세 정보
     * @return 생성된 약관 정보를 포함하는 ResponseEntity
     */
    @PostMapping
    @Operation(
            summary = "[ADMIN] 약관 생성",
            description = "관리자가 새로운 약관을 등록할 때 사용합니다."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "약관 생성 성공",
                    content = @Content(schema = @Schema(implementation = TermInfoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 입력값 (S002) - 필드 검증 실패 등",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 (S001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<TermInfoResponse> createTerm(
            @Valid @RequestBody TermSaveRequest request
    ) {
        return ResponseEntity.ok(userTermService.createTerm(request));
    }

    /**
     * 기존 약관 레코드를 수정합니다. 이 작업은 관리자 권한이 필요합니다.
     *
     * @param termId  수정할 약관의 ID
     * @param request 새로운 약관 상세 정보
     * @return 수정된 약관 정보를 포함하는 ResponseEntity
     */
    @PatchMapping
    @Operation(
            summary = "[ADMIN] 약관 수정",
            description = "특정 약관의 내용을 수정합니다. (관리자 전용)"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "약관 수정 성공",
                    content = @Content(schema = @Schema(implementation = TermInfoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 입력값 (S002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "관리자 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "약관을 찾을 수 없음 (S003 또는 별도 코드)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 (S001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<TermInfoResponse> updateTerm(
            @RequestParam Long termId,
            @Valid @RequestBody TermSaveRequest request
    ) {
        return ResponseEntity.ok(userTermService.updateTerm(termId, request));
    }
}
