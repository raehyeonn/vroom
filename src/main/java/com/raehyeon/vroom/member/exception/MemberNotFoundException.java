package com.raehyeon.vroom.member.exception;

import com.raehyeon.vroom.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends BaseException {

    public MemberNotFoundException() {
        super("존재하지 않거나 탈퇴한 사용자입니다.", HttpStatus.NOT_FOUND);
    }

}
