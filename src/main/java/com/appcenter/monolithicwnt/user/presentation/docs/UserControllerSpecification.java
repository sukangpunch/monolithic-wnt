package com.appcenter.monolithicwnt.user.presentation.docs;

import com.appcenter.monolithicwnt.global.dto.response.ErrorResponse;
import com.appcenter.monolithicwnt.global.dto.response.SuccessResponse;
import com.appcenter.monolithicwnt.user.dto.request.UserCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserControllerSpecification {

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

}
