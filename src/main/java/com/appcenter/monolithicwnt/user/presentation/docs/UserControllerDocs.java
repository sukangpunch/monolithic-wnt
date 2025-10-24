package com.appcenter.monolithicwnt.user.presentation.docs;

import com.appcenter.monolithicwnt.auth.presentation.AuthenticationPrincipal;
import com.appcenter.monolithicwnt.global.dto.response.ErrorResponse;
import com.appcenter.monolithicwnt.global.dto.response.SuccessResponse;
import com.appcenter.monolithicwnt.user.dto.request.Authentication;
import com.appcenter.monolithicwnt.user.dto.request.UserCreateRequest;
import com.appcenter.monolithicwnt.user.dto.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User API", description = "사용자 관련 API 명세")
public interface UserControllerDocs {

    // 유저 생성
    @Operation(summary = "유저 생성", description = "요청 정보를 바탕으로 유저를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = """
                    - [U-004] 유효하지 않은 이메일 형식입니다.
                    - [U-005] 유효하지 않은 비밀번호 형식입니다.
                    - [U-006] 유효하지 않은 닉네임 형식입니다.
                    - [U-007] 비밀번호가 일치하지 않습니다.
                    """,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "409", description = """
                    - [U-001] 이미 가입된 사용자입니다.
                    - [U-003] 이미 존재하는 닉네임입니다.
                    """,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/create")
    ResponseEntity<SuccessResponse<?>> createUser(@RequestBody UserCreateRequest request);

    // 유저 프로필 조회
    @Operation(summary = "프로필 조회", description = "사용자가 프로필을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = """
                    - [T-005] 토큰을 찾을 수 없습니다.
                    """,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = """
                    - [U-002] 존재하지 않는 사용자입니다.
                    """,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/me")
    ResponseEntity<SuccessResponse<UserProfileResponse>> getUserProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal Authentication authentication);
}
