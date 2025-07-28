package com.raehyeon.vroom.member.exception;

import com.raehyeon.vroom.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends BaseException {

    public DuplicateEmailException() {
        super("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT);
    }

}
