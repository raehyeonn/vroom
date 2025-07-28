package com.raehyeon.vroom.chat.exception;

import com.raehyeon.vroom.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidChatRoomPasswordException extends BaseException {

    public InvalidChatRoomPasswordException() {
        super("비밀번호가 일치하지않습니다.", HttpStatus.UNAUTHORIZED);
    }

}
