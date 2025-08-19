package com.raehyeon.vroom.chat.converter;

import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.dto.CreateChatRoomRequest;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomEntityConverter {

    public ChatRoom toEntity(CreateChatRoomRequest createChatRoomRequest, String code, String encodedPassword) {
        return ChatRoom.builder()
            .code(code)
            .name(createChatRoomRequest.getName())
            .hidden(createChatRoomRequest.isHidden())
            .passwordRequired(createChatRoomRequest.isPasswordRequired())
            .password(encodedPassword)
            .build();
    }

}
