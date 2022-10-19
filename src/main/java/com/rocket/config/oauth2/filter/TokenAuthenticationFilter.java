package com.rocket.config.oauth2.filter;

import com.rocket.config.oauth2.repository.RedisAuthTokenRepository;
import com.rocket.config.jwt.AuthToken;
import com.rocket.config.jwt.AuthTokenProvider;
import com.rocket.utils.CommonRequestContext;
import com.rocket.utils.HeaderUtil;
import io.netty.util.internal.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenProvider tokenProvider;
    private final CommonRequestContext commonRequestContext;
    private final RedisAuthTokenRepository authTokenRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)  throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String tokenStr = HeaderUtil.getAccessToken(request);
        AuthToken token = tokenProvider.convertAuthToken(tokenStr);
//        boolean exists = false;
//        String uuid = token.getUuid(request);
//
//        if (uuid != null) {
//            exists = authTokenRepository.existsById(uuid);
//        }

        // refresh token 체크 필요 만약 만료됐으면 로그아웃
        // 이상없이 완료되면 google, facebook login 구현
        if (!requestURI.equals("/api/v1/auth/healthcheck")) { // exists && token.validate()
            Authentication authentication = tokenProvider.getAuthentication(token, request);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String uuid = token.getUuid(request);
            commonRequestContext.setUuid(uuid);
        }

        filterChain.doFilter(request, response);
    }
}