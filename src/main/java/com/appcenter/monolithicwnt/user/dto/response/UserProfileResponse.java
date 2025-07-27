package com.appcenter.monolithicwnt.user.dto.response;

import com.appcenter.monolithicwnt.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 프로필 응답")
public record UserProfileResponse(
        @Schema(description = "사용자 닉네임", example = "kang")
        String nickname
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(user.getNickname());
    }
}
