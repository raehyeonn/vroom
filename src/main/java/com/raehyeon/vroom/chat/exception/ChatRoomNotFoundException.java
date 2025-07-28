package com.raehyeon.vroom.chat.exception;

import com.raehyeon.vroom.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ChatRoomNotFoundException extends BaseException {

    public ChatRoomNotFoundException() {
        super("존재하지 않거나 삭제된 채팅방입니다.", HttpStatus.NOT_FOUND);
    }

}
