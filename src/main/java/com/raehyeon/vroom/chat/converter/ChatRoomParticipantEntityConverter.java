package com.raehyeon.vroom.chat.converter;

import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.domain.ChatRoomParticipant;
import com.raehyeon.vroom.member.domain.Member;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomParticipantEntityConverter {

    public ChatRoomParticipant toEntity(Member member, ChatRoom chatRoom) {
        return ChatRoomParticipant.builder()
            .member(member)
            .chatRoom(chatRoom)
            .joinedAt(ZonedDateTime.now())
            .build();
    }

}
