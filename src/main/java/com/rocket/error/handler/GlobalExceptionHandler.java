package com.rocket.error.handler;

import com.rocket.error.exception.AuthException;
import com.rocket.error.exception.UserException;
import com.rocket.error.exception.UserFeedException;
import com.rocket.error.result.GlobalErrorResult;
import com.rocket.error.result.ValidErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            Exception.class
    })
    public ResponseEntity<?> handleException(Exception e) {
        log.error("Exception is occurred.", e);
        if (e instanceof MethodArgumentNotValidException) {
            ValidErrorResult result = ValidErrorResult.of(
                    ((MethodArgumentNotValidException) e).getBindingResult()
            );
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

        GlobalErrorResult result = GlobalErrorResult.of(e.getMessage());
        return new ResponseEntity<>(result, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<GlobalErrorResult> userExceptionHandler(UserException e) {
        GlobalErrorResult result = GlobalErrorResult.of(e.getErrorMessage());
        return new ResponseEntity<>(result, e.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(UserFeedException.class)
    public ResponseEntity<GlobalErrorResult> userFeedExceptionHandler(UserFeedException e) {
        GlobalErrorResult result = GlobalErrorResult.of(e.getErrorMessage());
        return new ResponseEntity<>(result, e.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<GlobalErrorResult> AuthExceptionHandler(AuthException e) {
        GlobalErrorResult result = GlobalErrorResult.of(e.getErrorMessage());
        return new ResponseEntity<>(result, e.getErrorCode().getHttpStatus());
    }
}
