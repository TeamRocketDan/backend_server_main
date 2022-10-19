package com.rocket.config.oauth2.filter;

import com.rocket.config.jwt.AuthToken;
import com.rocket.config.jwt.AuthTokenProvider;
import com.rocket.config.oauth2.repository.RedisAuthTokenRepository;
import com.rocket.utils.CommonRequestContext;
import com.rocket.utils.HeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenProvider tokenProvider;
    private final CommonRequestContext commonRequestContext;
    private final RedisTemplate redisTemplate;

    Set<String> urlSet = new HashSet<>(Arrays.asList(
            "/api/v1/auth/healthcheck",
            "/api/v1/auth/logout"
    ));

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)  throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String tokenStr = HeaderUtil.getAccessToken(request);
        AuthToken token = tokenProvider.convertAuthToken(tokenStr);

        if (!urlSet.contains(requestURI)
            && ObjectUtils.isEmpty(redisTemplate.opsForValue().get(tokenStr))) { // !requestURI.equals("/api/v1/auth/healthcheck")) {

            Authentication authentication = tokenProvider.getAuthentication(token, request);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String uuid = token.getUuid(request);
            commonRequestContext.setUuid(uuid);
        }

        filterChain.doFilter(request, response);
    }
}