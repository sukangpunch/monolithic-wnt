package com.appcenter.monolithicwnt.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("G-001", "오류가 발생했습니다. 관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR),

    // User
    USER_CONFLICT("U-001", "이미 가입된 사용자입니다.", HttpStatus.CONFLICT),
    USER_NOT_FOUND("U-002", "아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.NOT_FOUND),
    USER_NICKNAME_CONFLICT("U-003", "이미 존재하는 닉네임입니다.", HttpStatus.CONFLICT),
    EMAIL_INVALID("U-004", "유효하지 않은 이메일 형식입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID("U-005", "유효하지 않은 비밀번호 형식입니다.", HttpStatus.BAD_REQUEST),
    NICKNAME_INVALID("U-006", "유효하지 않은 닉네임 형식입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCHED("U-007", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    USER_UNAUTHORIZED("U-008", "권한 없는 사용자입니다.", HttpStatus.UNAUTHORIZED),

    // Store
    STORE_CONFLICT("S-001","이미 등록된 가게입니다.", HttpStatus.CONFLICT),
    STORE_NOT_FOUND("S-002","가게가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    STORE_NAME_CONFLICT("S-003", "이미 존재하는 가게 이름입니다.", HttpStatus.CONFLICT),
    STORE_NAME_INVALID("S-004", "유효하지 않은 가게 이름입니다.", HttpStatus.BAD_REQUEST),
    STORE_PHONE_INVALID("S-005","유효하지 않은 전화번호입니다.", HttpStatus.BAD_REQUEST),
    STORE_INSTAGRAM_INVALID("S-006","유효하지 않은 인스타그램 아이디입니다.", HttpStatus.BAD_REQUEST),
    STORE_ADDRESS_INVALID("S-007", "유효하지 않은 주소입니다.", HttpStatus.BAD_REQUEST),
    STORE_LATITUDE_INVALID("S-008", "유효하지 않은 위도입니다.", HttpStatus.BAD_REQUEST),
    STORE_LONGITUDE_INVALID("S-009", "유효하지 않은 경도입니다.", HttpStatus.BAD_REQUEST),
    STORE_OPEN_CLOSE_INVALID("S-010", "유효하지 않은 Open-Close 시간대 입니다.", HttpStatus.BAD_REQUEST),
    STORE_SLOT_TIME_INVALID("S-11", "유효하지 않은 Slot-Time 입니다.", HttpStatus.BAD_REQUEST),
    STORE_BREAK_START_END_INVALID("S-12", "유효하지 않은 Break-Time 입니다.", HttpStatus.BAD_REQUEST),
    STORE_HOLIDAY_INVALID("S-13", "유효하지 않은 Holiday 입니다.", HttpStatus.BAD_REQUEST),

    // Menu
    MENU_NAME_INVALID("M-001", "유효하지 않은 메뉴 이름입니다.", HttpStatus.BAD_REQUEST),
    MENU_DETAILS_INVALID("M-002","유효하지 않은 메뉴 설명입니다.", HttpStatus.BAD_REQUEST),
    MENU_NOT_FOUND("M-003","존재하지 않는 메뉴입니다.", HttpStatus.NOT_FOUND),
    MENU_PRICE_INVALID("M-004","유효하지 않은 메뉴 가격입니다.", HttpStatus.BAD_REQUEST),
    MENU_DETAIL_LENGTH_INVALID("M-005", "유효하지 않은 메뉴 길이입니다.", HttpStatus.BAD_REQUEST),

    // JWT
    TOKEN_INVALID("T-001", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("T-002", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EMPTY("T-003", "빈 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_SIGNED("T-004", "서명되지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_FOUND("T-005", "토큰을 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),

    // S3
    S3_SERVICE_EXCEPTION("S3-001", "S3 서비스 예외가 발생했습니다.", HttpStatus.BAD_REQUEST),
    S3_CLIENT_EXCEPTION("S3-002", "S3 클라이언트 예외가 발생했습니다.", HttpStatus.BAD_REQUEST),
    NOT_ALLOWED_FILE_EXTENSIONS("S3-003","허용되지 않은 확장자입니다.",HttpStatus.BAD_REQUEST),
    INVALID_FILE_EXTENSIONS("S3-004", "파일 형식이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    ;


    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}
