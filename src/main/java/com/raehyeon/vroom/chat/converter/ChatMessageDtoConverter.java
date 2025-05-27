package com.raehyeon.vroom.chat.converter;

import com.raehyeon.vroom.chat.domain.ChatMessage;
import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.dto.SendChatMessageRequest;
import com.raehyeon.vroom.chat.dto.SendChatMessageResponse;
import com.raehyeon.vroom.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageDtoConverter {

    public SendChatMessageResponse toSendChatMessageResponse(ChatRoom chatRoom, Member member, ChatMessage chatMessage) {
        return SendChatMessageResponse.builder()
            .chatRoomId(chatRoom.getId())
            .senderId(member.getId())
            .senderNickname(member.getNickname())
            .content(chatMessage.getContent())
            .sentAt(chatMessage.getSentAt())
            .build();
    }

}
