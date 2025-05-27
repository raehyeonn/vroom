package com.raehyeon.vroom.chat.converter;

import com.raehyeon.vroom.chat.domain.ChatMessage;
import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.dto.SendChatMessageRequest;
import com.raehyeon.vroom.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageEntityConverter {

    public ChatMessage toEntity(ChatRoom chatRoom, Member member, SendChatMessageRequest sendChatMessageRequest) {
        return ChatMessage.builder()
            .chatRoom(chatRoom)
            .sender(member)
            .content(sendChatMessageRequest.getContent())
            .build();
    }

}
