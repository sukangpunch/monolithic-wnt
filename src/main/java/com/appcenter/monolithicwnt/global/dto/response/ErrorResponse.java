package com.appcenter.monolithicwnt.global.dto.response;

import com.appcenter.monolithicwnt.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예외 공통 응답 DTO")
public record ErrorResponse(
        @Schema(description = "에러 코드", examples = "U-001")
        String code,
        @Schema(description = "예외 메시지", examples = "이미 가입된 사용자입니다.")
        String message,
        @Schema(description = "상태 코드", examples = "409")
        int status
) {

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(),errorCode.getMessage(),errorCode.getStatus().value());
    }
}
