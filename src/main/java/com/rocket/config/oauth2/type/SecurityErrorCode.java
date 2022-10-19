package com.rocket.config.oauth2.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;

@Getter
@AllArgsConstructor
public enum SecurityErrorCode {

    EXPIRED_TOKEN(HttpServletResponse.SC_BAD_REQUEST, "만료된 토큰입니다.", "EXPIRED_TOKEN"),
    UNAUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED, "인증되지 않은 유저입니다.", "UNAUTHORIZED"),
    INVALIDED(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 토큰입니다.", "INVALIDED");

    private final int httpStatus;
    private final String message;
    private final String code;
}
