package com.rocket.utils;

import com.rocket.error.result.GlobalErrorResult;
import lombok.*;

public class ApiUtils {

    public static <T> ApiResult<T> success(T response) {
        return new ApiResult<>(true, response);
    }

    @Getter
    @Setter
    public static class ApiResult<T> {
        private final boolean success;
        private final T result;

        private ApiResult(boolean success, T response) {
            this.success = success;
            this.result = response;
        }
    }
}
