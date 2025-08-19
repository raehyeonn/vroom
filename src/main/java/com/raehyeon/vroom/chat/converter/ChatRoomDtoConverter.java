package com.raehyeon.vroom.chat.converter;

import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.domain.ChatRoomParticipant;
import com.raehyeon.vroom.chat.dto.UpdateChatRoomNameResponse;
import com.raehyeon.vroom.chat.dto.JoinChatRoomResponse;
import com.raehyeon.vroom.chat.dto.CreateChatRoomResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomSummaryResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomParticipantResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomDetailResponse;
import com.raehyeon.vroom.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomDtoConverter {

    public CreateChatRoomResponse toCreateChatRoomResponse(ChatRoom chatRoom) {
        return CreateChatRoomResponse.builder()
            .id(chatRoom.getId())
            .code(chatRoom.getCode())
            .name(chatRoom.getName())
            .createdAt(chatRoom.getCreatedAt())
            .build();
    }

    public GetChatRoomSummaryResponse toGetChatRoomSummaryResponse(ChatRoom chatRoom) {
        return GetChatRoomSummaryResponse.builder()
            .id(chatRoom.getId())
            .name(chatRoom.getName())
            .passwordRequired(chatRoom.isPasswordRequired())
            .build();
    }

    public GetChatRoomDetailResponse toGetChatRoomDetailResponse(ChatRoom chatRoom) {
        return GetChatRoomDetailResponse.builder()
            .code(chatRoom.getCode())
            .name(chatRoom.getName())
            .build();
    }

    public UpdateChatRoomNameResponse toUpdateChatRoomNameResponse(ChatRoom chatRoom) {
        return UpdateChatRoomNameResponse.builder()
            .name(chatRoom.getName())
            .build();
    }

    public JoinChatRoomResponse toJoinChatRoomResponse(boolean success) {
        return JoinChatRoomResponse.builder()
            .success(success)
            .build();
    }

    public GetChatRoomParticipantResponse toGetChatRoomParticipantResponse(ChatRoomParticipant chatRoomParticipant) {
        Member member = chatRoomParticipant.getMember();

        return GetChatRoomParticipantResponse.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .build();
    }

}
