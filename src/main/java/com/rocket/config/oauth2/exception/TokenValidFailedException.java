package com.rocket.config.oauth2.exception;

public class TokenValidFailedException extends RuntimeException {
    public TokenValidFailedException() {
        super("Failed to generate RedisAuthToken.");
    }

    private TokenValidFailedException(String message) {
        super(message);
    }
}
