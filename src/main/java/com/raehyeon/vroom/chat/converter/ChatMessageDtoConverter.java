package com.raehyeon.vroom.chat.converter;

import com.raehyeon.vroom.chat.domain.ChatMessage;
import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.dto.ChatMessageDto;
import com.raehyeon.vroom.chat.dto.GetPastChatMessagesResponse;
import com.raehyeon.vroom.chat.dto.SendChatMessageResponse;
import com.raehyeon.vroom.member.domain.Member;
import java.time.ZonedDateTime;
import java.util.List;
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

    public ChatMessageDto toChatMessageDto(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
            .chatMessageId(chatMessage.getId())
            .senderId(chatMessage.getSender().getId())
            .senderNickname(chatMessage.getSender().getNickname())
            .content(chatMessage.getContent())
            .sentAt(chatMessage.getSentAt())
            .build();
    }

    public GetPastChatMessagesResponse toGetPastChatMessagesResponse(List<ChatMessage> chatMessages, ZonedDateTime nextCursor, boolean hasNext) {
        List<ChatMessageDto> dtoList = chatMessages.stream().map(this::toChatMessageDto).toList();

        return GetPastChatMessagesResponse.builder()
            .chatMessages(dtoList)
            .nextCursor(nextCursor)
            .hasNext(hasNext)
            .build();
    }

}
