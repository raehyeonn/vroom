package com.raehyeon.vroom.follow.exception;

import com.raehyeon.vroom.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AlreadyFollowingException extends BaseException {

    public AlreadyFollowingException() {
        super("이미 팔로우한 사용자입니다.", HttpStatus.CONFLICT);
    }

}
