package com.product.model;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ProductServiceException extends RuntimeException{
    private final LocalDateTime timestamp;
    private final Integer errorCode;
    private final HttpStatus httpStatus;

    public ProductServiceException(@NonNull HttpStatus httpStatus, @NonNull Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public ProductServiceException(@NonNull HttpStatus httpStatus, @NonNull Integer errorCode, String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
