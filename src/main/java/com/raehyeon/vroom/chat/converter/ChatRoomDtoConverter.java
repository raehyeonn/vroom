package com.raehyeon.vroom.chat.converter;

import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.dto.CreateChatRoomResponse;
import com.raehyeon.vroom.chat.dto.GetAllChatRoomsResponse;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomDtoConverter {

    public GetAllChatRoomsResponse toGetAllChatRoomsResponse(ChatRoom chatRoom) {
        return GetAllChatRoomsResponse.builder()
            .id(chatRoom.getId())
            .name(chatRoom.getName())
            .build();
    }

    public CreateChatRoomResponse toCreateChatRoomResponse(ChatRoom chatRoom) {
        return CreateChatRoomResponse.builder()
            .id(chatRoom.getId())
            .name(chatRoom.getName())
            .code(chatRoom.getCode())
            .createdAt(chatRoom.getCreatedAt())
            .build();
    }

}
