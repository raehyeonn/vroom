package com.raehyeon.vroom.chat.converter;

import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.domain.ChatRoomParticipant;
import com.raehyeon.vroom.chat.dto.ChangeRoomNameResponse;
import com.raehyeon.vroom.chat.dto.ChatRoomEntryResponse;
import com.raehyeon.vroom.chat.dto.CreateChatRoomResponse;
import com.raehyeon.vroom.chat.dto.GetAllChatRoomsResponse;
import com.raehyeon.vroom.chat.dto.GetAllParticipantsResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomByCodeResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomDetailResponse;
import com.raehyeon.vroom.chat.dto.GetMyChatRoomListResponse;
import com.raehyeon.vroom.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomDtoConverter {

    public GetAllChatRoomsResponse toGetAllChatRoomsResponse(ChatRoom chatRoom) {
        return GetAllChatRoomsResponse.builder()
            .id(chatRoom.getId())
            .name(chatRoom.getName())
            .passwordRequired(chatRoom.isPasswordRequired())
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

    public GetChatRoomDetailResponse toGetChatRoomDetailResponse(ChatRoom chatRoom) {
        return GetChatRoomDetailResponse.builder()
            .roomName(chatRoom.getName())
            .build();
    }

    public GetChatRoomByCodeResponse toGetChatRoomByCodeResponse(ChatRoom chatRoom) {
        return GetChatRoomByCodeResponse.builder()
            .id(chatRoom.getId())
            .name(chatRoom.getName())
            .build();
    }

    public ChatRoomEntryResponse toChatRoomEntryResponse(boolean result) {
        return ChatRoomEntryResponse.builder()
            .result(result)
            .build();
    }

    public GetMyChatRoomListResponse toGetMyChatRoomListResponse(ChatRoomParticipant chatRoomParticipant) {
        ChatRoom chatRoom = chatRoomParticipant.getChatRoom();

        return GetMyChatRoomListResponse.builder()
            .id(chatRoom.getId())
            .name(chatRoom.getName())
            .passwordRequired(chatRoom.isPasswordRequired())
            .build();
    }

    public ChangeRoomNameResponse toChangeRoomNameResponse(ChatRoom chatRoom) {
        return ChangeRoomNameResponse.builder()
            .roomName(chatRoom.getName())
            .build();
    }

    public GetAllParticipantsResponse toGetAllParticipantsResponse(ChatRoomParticipant chatRoomParticipant) {
        Member member = chatRoomParticipant.getMember();

        return GetAllParticipantsResponse.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .build();
    }

}
