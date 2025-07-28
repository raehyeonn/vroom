package com.raehyeon.vroom.chat.exception;

import com.raehyeon.vroom.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ChatRoomPasswordRequiredException extends BaseException {

    public ChatRoomPasswordRequiredException() {
        super("채팅방 비밀번호가 필요합니다.", HttpStatus.BAD_REQUEST);
    }

}
