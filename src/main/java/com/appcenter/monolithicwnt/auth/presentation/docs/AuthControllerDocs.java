package com.appcenter.monolithicwnt.auth.presentation.docs;

import com.appcenter.monolithicwnt.auth.dto.request.LoginRequest;
import com.appcenter.monolithicwnt.global.dto.response.ErrorResponse;
import com.appcenter.monolithicwnt.global.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth API", description = "인증/인가 관련 API 명세")
public interface AuthControllerDocs {
    // 로그인
    @Operation(summary = "로그인", description = "사용자 로그인을 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = """
                    - [T-001] 유효하지 않은 토큰입니다.
                    - [T-002] 만료된 토큰입니다.
                    - [T-003] 빈 토큰입니다.
                    - [T-004] 서명되지 않은 토큰입니다.
                    """,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = """
                    - [U-002] 아이디 또는 비밀번호가 일치하지 않습니다.
                    """,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    ResponseEntity<SuccessResponse<Void>> login(@RequestBody LoginRequest request);

    // 로그아웃
    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    @PostMapping("/logout")
    ResponseEntity<SuccessResponse<Void>> logout();
}
