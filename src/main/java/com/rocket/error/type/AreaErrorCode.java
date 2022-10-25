package com.rocket.error.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AreaErrorCode {

    AREA_NOT_FOUND(HttpStatus.BAD_REQUEST, "지역을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
