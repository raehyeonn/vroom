package com.raehyeon.vroom.chat.exception;

import com.raehyeon.vroom.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ChatRoomParticipantNotFoundException extends BaseException {

    public ChatRoomParticipantNotFoundException() {
        super("채팅방 참여자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }

}
