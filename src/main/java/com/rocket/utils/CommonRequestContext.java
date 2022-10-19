package com.rocket.utils;

import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CommonRequestContext {

    private String uuid;
}
