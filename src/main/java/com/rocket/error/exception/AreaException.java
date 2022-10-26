package com.rocket.error.exception;

import com.rocket.error.type.AreaErrorCode;
import com.rocket.error.type.UserErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AreaException extends RuntimeException {
    private AreaErrorCode errorCode;
    private String errorMessage;

    public AreaException(AreaErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }

}
