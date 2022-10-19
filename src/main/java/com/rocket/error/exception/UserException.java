package com.rocket.error.exception;

import com.rocket.error.type.UserErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserException extends RuntimeException {
    private UserErrorCode errorCode;
    private String errorMessage;

    public UserException(UserErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }

}
