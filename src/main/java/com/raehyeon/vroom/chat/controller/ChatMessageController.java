package com.raehyeon.vroom.chat.controller;

import com.raehyeon.vroom.chat.dto.GetPastChatMessagesResponse;
import com.raehyeon.vroom.chat.dto.SendChatMessageRequest;
import com.raehyeon.vroom.chat.service.ChatMessageService;
import java.security.Principal;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/api/chat-rooms/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") Long chatRoomId, Principal principal, @Payload SendChatMessageRequest sendChatMessageRequest) {
        chatMessageService.createMessage(chatRoomId, principal, sendChatMessageRequest);
    }

    @GetMapping("/api/chat-rooms/{chatRoomId}/messages")
    public GetPastChatMessagesResponse getPastChatMessages(@PathVariable Long chatRoomId, @RequestParam(required = false) ZonedDateTime cursor) {
        return chatMessageService.getMessages(chatRoomId, cursor);
    }

}
