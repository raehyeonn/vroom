package com.raehyeon.vroom.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 모든 컨트롤러에서 발생하는 예외를 공통 처리합니다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 요청 본문 유효성 검사 실패 시 발생하는 예외를 처리합니다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException) {
        String message = "요청값이 유효하지 않습니다.";
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        int statusCode = httpStatus.value();
        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();

        ErrorResponse errorResponse = ErrorResponse.ofFieldErrors(message, statusCode, bindingResult);

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    // 요청 파라미터 유효성 검사 실패 시 발생하는 예외를 처리합니다.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException constraintViolationException) {
        String message = "요청 파라미터 값이 유효하지 않습니다.";
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        int statusCode = httpStatus.value();
        Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();

        ErrorResponse errorResponse = ErrorResponse.ofConstraintViolations(message, statusCode, constraintViolations);

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    // 커스텀 예외를 처리합니다.
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBase(BaseException baseException) {
        String message = baseException.getMessage();
        HttpStatus httpStatus = baseException.getHttpStatus();
        int statusCode = httpStatus.value();

        ErrorResponse errorResponse = ErrorResponse.ofStatusAndMessage(message, statusCode);

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

}
