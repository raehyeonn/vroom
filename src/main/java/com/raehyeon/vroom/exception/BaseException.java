package com.raehyeon.vroom.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 커스텀 예외들의 공통 부모 클래스입니다.
@Getter
public class BaseException extends RuntimeException {

    private final HttpStatus httpStatus;

    public BaseException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
