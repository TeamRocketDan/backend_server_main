package com.rocket.config.oauth2.exception;

import com.rocket.config.oauth2.type.SecurityErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.rocket.config.oauth2.type.SecurityErrorCode.*;

@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        String exception = (String)request.getAttribute("exception");

        if(exception == null) {
            SecurityErrorCode errorCode = UNAUTHORIZED;
            setResponse(response, errorCode);
            return;
        } else if (exception.equals(EXPIRED_TOKEN.getCode())) {
            SecurityErrorCode errorCode = EXPIRED_TOKEN;
            setResponse(response, errorCode);
        } else if (exception.equals(INVALIDED.getCode())) {
            SecurityErrorCode errorCode = INVALIDED;
            setResponse(response, errorCode);
        }

    }

    private void setResponse(HttpServletResponse response, SecurityErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getHttpStatus());
        response.getWriter().println("{\"success\":false," +
                "\"result\":null,\"" +
                "errorMessage\":\"" + errorCode.getMessage() +"\"}"
        );
    }
}
