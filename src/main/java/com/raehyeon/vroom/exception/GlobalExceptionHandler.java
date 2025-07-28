package com.raehyeon.vroom.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException) {
        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
        ErrorResponse errorResponse = ErrorResponse.fieldValidationErrors("요청값이 유효하지 않습니다.", bindingResult);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException constraintViolationException) {
        ErrorResponse errorResponse = ErrorResponse.ofConstraintViolation("요청 파라미터 값이 유효하지 않습니다.", constraintViolationException.getConstraintViolations());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBase(BaseException baseException) {
        ErrorResponse errorResponse = ErrorResponse.of(baseException.getHttpStatus(), baseException.getMessage());

        return ResponseEntity.status(baseException.getHttpStatus()).body(errorResponse);
    }

}
