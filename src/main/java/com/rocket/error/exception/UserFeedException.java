package com.rocket.error.exception;

import com.rocket.error.type.UserFeedErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFeedException extends RuntimeException {
    private UserFeedErrorCode errorCode;
    private String errorMessage;

    public UserFeedException(UserFeedErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }
}
