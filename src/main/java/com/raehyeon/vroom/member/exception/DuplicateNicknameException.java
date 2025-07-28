package com.raehyeon.vroom.member.exception;

import com.raehyeon.vroom.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicateNicknameException extends BaseException {

    public DuplicateNicknameException() {
        super("이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT);
    }

}
