package com.raehyeon.vroom.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값을 갖는 필드는 JSON에 포함시키지 않는다.
public class ErrorResponse {

    private final int status;
    private final String message;
    private final List<ValidationError> errors;

    public ErrorResponse(int status, String message, List<ValidationError> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    /**
     * HTTP 상태 코드와 에러 메시지를 포함해 에러 응답을 생성합니다.
     *
     * @param status HTTP 상태 코드
     * @param message 클라이언트에게 전달할 에러 메시지
     * @return HTTP 상태 코드와 에러 메시지를 포함한 에러 응답
     */
    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status.value(), message, null);
    }

    /**
     * 요청 본문(@RequestBody)에 대한 필드 검증 오류 정보를 포함해 에러 응답을 생성합니다.
     *
     * @param message 클라이언트에게 전달할 에러 메시지
     * @param bindingResult 요청 본문의 필드 검증 결과가 담긴 BindingResult 객체
     * @return 검증 실패한 요청 본문의 각 필드별 오류 메시지를 포함한 에러 응답
     */
    public static ErrorResponse fieldValidationErrors(String message, BindingResult bindingResult) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message, ValidationError.ofFieldErrors(bindingResult.getFieldErrors()));
    }

    /**
     * 요청 파라미터(@RequestParam, @PathVariable)의 제약 조건 위반 정보를 포함해 에러 응답을 생성합니다.
     *
     * @param message 클라이언트에게 전달할 에러 메시지
     * @param violations 제약 조건을 위반한 항목 목록
     * return 파라미터 유효성 검사 실패 정보를 포함한 에러 응답
     */
    public static ErrorResponse ofConstraintViolation(String message, Set<ConstraintViolation<?>> violations) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message, ValidationError.ofConstraintViolations(violations));
    }

}
