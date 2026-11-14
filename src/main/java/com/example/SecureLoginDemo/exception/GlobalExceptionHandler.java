package com.example.SecureLoginDemo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TooManyLoginAttemptException.class)
    public ResponseEntity<String> handleTooManyLoginAttempts(TooManyLoginAttemptException exception){
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handelRuntimeException(RuntimeException exception){

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(exception.getMessage());
    }
}
