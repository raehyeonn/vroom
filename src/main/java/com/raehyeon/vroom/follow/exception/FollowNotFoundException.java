package com.raehyeon.vroom.follow.exception;

import com.raehyeon.vroom.exception.BaseException;
import org.springframework.http.HttpStatus;

public class FollowNotFoundException extends BaseException {

    public FollowNotFoundException() {
        super("팔로우 관계가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
    }

}
