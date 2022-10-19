package com.rocket.config.oauth2.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenAccessDeniedHandler implements AccessDeniedHandler {

//    private final HandlerExceptionResolver handlerExceptionResolver;

    static String _403 = "{\"success\":false,\"result\":null,\"errorMessage\":\"접근 권한이 없는 유저입니다.\"}";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setHeader("content-type", "application/json");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(_403);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
