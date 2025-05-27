package com.raehyeon.vroom.chat.controller;

import com.raehyeon.vroom.chat.dto.SendChatMessageRequest;
import com.raehyeon.vroom.chat.service.ChatMessageService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/api/chat-rooms/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") Long chatRoomId, Principal principal, @Payload SendChatMessageRequest sendChatMessageRequest) {
        chatMessageService.createMessage(chatRoomId, principal, sendChatMessageRequest);
    }

}
