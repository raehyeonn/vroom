package com.raehyeon.vroom.chat.converter;

import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.dto.CreateChatRoomRequest;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomEntityConverter {

    public ChatRoom toEntity(CreateChatRoomRequest createChatRoomRequest, String chatRoomCode) {
        return ChatRoom.builder()
            .name(createChatRoomRequest.getName())
            .code(chatRoomCode)
            .build();
    }

}
