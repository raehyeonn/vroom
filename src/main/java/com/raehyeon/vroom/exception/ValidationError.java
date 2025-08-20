package com.raehyeon.vroom.exception;

import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;

// 유효성 검사 실패 정보를 담는 DTO 역할을 합니다.
@Getter
@RequiredArgsConstructor
public class ValidationError {

    private final String field; // 유효성 검사에 실패한 필드 또는 파라미터
    private final Object rejectedValue; // 사용자가 입력한 잘못된 값
    private final String reason; // 유효성 검사를 실패한 이유

    // 요청 본문 유효성 검사를 실패한 필드에 대한 정보를 받습니다.
    // 각 필드 오류 정보를 ValidationError 객체 리스트로 변환합니다.
    public static List<ValidationError> ofFieldErrors(List<FieldError> fieldErrors) {
        return fieldErrors.stream().map(fieldError -> new ValidationError(
            fieldError.getField(),
            fieldError.getRejectedValue(),
            fieldError.getDefaultMessage()
        )).toList();
    }

    // 요청 파라미터 유효성 검사를 실패한 제약 조건 위반 정보를 받습니다.
    // 각 위반 사항을 ValidationError 객체 리스트로 변환합니다.
    public static List<ValidationError> ofConstraintViolations(Set<ConstraintViolation<?>> constraintViolations) {
        return constraintViolations.stream().map(violation -> new ValidationError(
            violation.getPropertyPath().toString(),
            violation.getInvalidValue(),
            violation.getMessage()
        )).toList();
    }

}
