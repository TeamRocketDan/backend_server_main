package com.rocket.auth.controller;

import com.rocket.auth.dto.TokenResponse;
import com.rocket.config.jwt.AuthToken;
import com.rocket.config.jwt.AuthTokenProvider;
import com.rocket.config.oauth2.entity.RoleType;
import com.rocket.config.properties.AppProperties;
import com.rocket.error.exception.AuthException;
import com.rocket.error.exception.UserException;
import com.rocket.user.user.entity.UserRefreshToken;
import com.rocket.user.user.repository.UserRefreshTokenRepository;
import com.rocket.utils.ApiUtils.ApiResult;
import com.rocket.utils.HeaderUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static com.rocket.error.type.AuthErrorCode.*;
import static com.rocket.error.type.UserErrorCode.USER_NOT_FOUND;
import static com.rocket.utils.ApiUtils.success;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    private final static long THREE_DAYS_MSEC = 604800000;

    @GetMapping("/healthcheck")
    public ApiResult healthcheck() {

        return success(null);
    }

    @GetMapping("/refresh")
    @Transactional
    public ApiResult refreshToken (HttpServletRequest request, HttpServletResponse response) {
        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);

        if (!authToken.validate(request)) {
            throw new AuthException(INVALID_ACCESS_TOKEN);
        }

        Claims claims = authToken.getExpiredTokenClaims(request);

        if (claims == null) {
            throw new AuthException(NOT_YET_EXPIRED_TOKEN);
        }

        String userId = claims.getSubject();
        RoleType roleType = RoleType.of(claims.get("role", String.class));
        String uuid = authToken.getUuid(request);

        UserRefreshToken refreshToken = userRefreshTokenRepository.findByUuid(uuid)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken.getRefreshToken());

        if (!authRefreshToken.validate(request)) {
            throw new AuthException(INVALID_REFRESH_TOKEN);
        }

        Date now = new Date();
        AuthToken newAccessToken = tokenProvider.createAuthToken(
                userId,
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        long validTime = authRefreshToken.getTokenClaims(request).getExpiration().getTime() - now.getTime();

        if (validTime <= THREE_DAYS_MSEC) {
            // refresh 토큰 설정
            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

            authRefreshToken = tokenProvider.createAuthToken(
                    appProperties.getAuth().getTokenSecret(),
                    new Date(now.getTime() + refreshTokenExpiry)
            );

            // DB에 refresh 토큰 업데이트
            refreshToken.updateRefreshToken(authRefreshToken.getToken());
        }

        return success(new TokenResponse(newAccessToken.getToken()));
    }
}
