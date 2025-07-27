package com.appcenter.monolithicwnt.global.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

@Schema(description = "성공 공통 응답 dto")
public record SuccessResponse<T>(
        @Schema(description = "상태 코드", examples = "200")
        int status,
        @Schema(description = "응답 데이터")
        T data
) {
    public static <T> SuccessResponse<T> of(HttpStatus httpStatus, T data) {
        return new SuccessResponse<>(httpStatus.value(), data);
    }
}
