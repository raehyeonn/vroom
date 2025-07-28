package com.raehyeon.vroom.security.exception;

import com.raehyeon.vroom.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BaseException {

    public TokenExpiredException() {
        super("액세스 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED);
    }

}
