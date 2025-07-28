package com.raehyeon.vroom.exception;

import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.springframework.validation.FieldError;

@Getter
public class ValidationError {

    private final String field; // 에러가 발생한 필드 이름
    private final Object rejectedValue; // 사용자가 입력한 잘못된 값
    private final String reason; // 에러 사유

    public ValidationError(String field, Object rejectedValue, String reason) {
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.reason = reason;
    }

    public static List<ValidationError> ofFieldErrors(List<FieldError> fieldErrors) {
        return fieldErrors.stream().map(fieldError -> new ValidationError(
            fieldError.getField(),
            fieldError.getRejectedValue() == null ? "" : fieldError.getRejectedValue(),
            fieldError.getDefaultMessage()
        )).toList();
    }

    public static List<ValidationError> ofConstraintViolations(
        Set<ConstraintViolation<?>> constraintViolations) {
        return constraintViolations.stream().map(violation -> new ValidationError(
            violation.getPropertyPath().toString(),
            violation.getInvalidValue(),
            violation.getMessage()
        )).toList();
    }

}
