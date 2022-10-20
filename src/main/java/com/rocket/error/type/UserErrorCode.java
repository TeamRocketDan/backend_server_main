package com.rocket.error.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode {

    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."),
    USER_DELETED_AT(HttpStatus.BAD_REQUEST, "이미 탈퇴한 사용자 입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
