package com.raehyeon.vroom.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;

// 예외 발생 시 클라이언트에게 반환할 에러 정보를 담는 DTO 역할을 합니다.
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
public class ErrorResponse {

    private final String message; // 에러 메시지
    private final int statusCode; // HTTP 상태 코드
    private final List<ValidationError> validationErrors; // 유효성 검사 실패 정보 리스트

    public static ErrorResponse ofFieldErrors(String message, int statusCode, BindingResult bindingResult) {
        return new ErrorResponse(message, statusCode, ValidationError.ofFieldErrors(bindingResult.getFieldErrors()));
    }

    public static ErrorResponse ofConstraintViolations(String message, int statusCode, Set<ConstraintViolation<?>> violations) {
        return new ErrorResponse(message, statusCode, ValidationError.ofConstraintViolations(violations));
    }

    public static ErrorResponse ofStatusAndMessage(String message, int statusCode) {
        return new ErrorResponse(message, statusCode, null);
    }

}
