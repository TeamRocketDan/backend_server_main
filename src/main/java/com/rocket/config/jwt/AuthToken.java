package com.rocket.config.jwt;

import com.rocket.config.oauth2.type.SecurityErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class AuthToken {

    @Getter
    private final String token;
    private final Key key;

    private static final String AUTHORITIES_KEY = "role";

    AuthToken(String id, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, expiry);
    }

    AuthToken(String id, String role, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, role, expiry);
    }

    private String createAuthToken(String id, Date expiry) {
        return Jwts.builder()
                .setSubject(id)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    private String createAuthToken(String id, String role, Date expiry) {
        return Jwts.builder()
                .setSubject(id)
                .claim(AUTHORITIES_KEY, role)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    public boolean validate(HttpServletRequest request) {
        return this.getTokenClaims(request) != null;
    }

    public String getUuid(HttpServletRequest request) {
        Claims claims = getTokenClaims(request);
        return claims == null ? null : claims.getSubject();
    }

    public long getExpiration(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody().getExpiration();

        long now = new Date().getTime();

        return expiration.getTime() - now;
    }


    public Claims getTokenClaims(HttpServletRequest request) {
        try {

            return Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token).getBody();
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
            request.setAttribute("exception", SecurityErrorCode.INVALIDED.getCode());
        } catch (ExpiredJwtException e) {
            if (request.getRequestURI().equals("/api/v1/auth/refresh")) {
                return e.getClaims();
            } else {
                request.setAttribute("exception", SecurityErrorCode.EXPIRED_TOKEN.getCode());
            }
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return null;
    }

    public Claims getExpiredTokenClaims(HttpServletRequest request) {
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            if (request.getRequestURI().equals("/api/v1/auth/refresh")) {
                return e.getClaims();
            } else {
                request.setAttribute("exception", SecurityErrorCode.EXPIRED_TOKEN.getCode());
            }
        }
        return null;
    }
}
