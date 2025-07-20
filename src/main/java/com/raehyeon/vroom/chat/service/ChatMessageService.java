package com.raehyeon.vroom.chat.service;

import com.raehyeon.vroom.chat.converter.ChatMessageDtoConverter;
import com.raehyeon.vroom.chat.converter.ChatMessageEntityConverter;
import com.raehyeon.vroom.chat.domain.ChatMessage;
import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.dto.GetPastChatMessagesResponse;
import com.raehyeon.vroom.chat.dto.SendChatMessageRequest;
import com.raehyeon.vroom.chat.dto.SendChatMessageResponse;
import com.raehyeon.vroom.chat.exception.ChatRoomNotFoundException;
import com.raehyeon.vroom.chat.repository.ChatMessageRepository;
import com.raehyeon.vroom.chat.repository.ChatRoomRepository;
import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.exception.MemberNotFoundException;
import com.raehyeon.vroom.member.repository.MemberRepository;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageDtoConverter chatMessageDtoConverter;
    private final ChatMessageEntityConverter chatMessageEntityConverter;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public void createMessage(Long chatRoomId, Principal principal, SendChatMessageRequest sendChatMessageRequest) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ChatRoomNotFoundException("존재하지 않거나 삭제된 채팅방입니다."));

        String email = principal.getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new MemberNotFoundException("존재하지 않거나 탈퇴한 사용자입니다."));

        ChatMessage chatMessage = chatMessageEntityConverter.toEntity(chatRoom, member, sendChatMessageRequest);
        chatMessageRepository.save(chatMessage);

        SendChatMessageResponse sendChatMessageResponse = chatMessageDtoConverter.toSendChatMessageResponse(chatRoom, member, chatMessage);
        simpMessagingTemplate.convertAndSend("/sub/" + chatRoomId, sendChatMessageResponse);
    }


    public GetPastChatMessagesResponse getMessages(Long chatRoomId, ZonedDateTime cursor) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ChatRoomNotFoundException("존재하지 않거나 삭제된 채팅방입니다."));

        Pageable limit = PageRequest.of(0, 21);

        List<ChatMessage> messages;
        if(cursor == null) {
            messages = chatMessageRepository.findTop21ByChatRoomOrderBySentAtDesc(chatRoom, limit);
        } else {
            messages = chatMessageRepository.findTop21ByChatRoomAndSentAtBeforeOrderBySentAtDesc(chatRoom, cursor, limit);
        }

        boolean hasNext = messages.size() > 20;
        ZonedDateTime nextCursor = hasNext ? messages.get(19).getSentAt() : null;

        List<ChatMessage> resultMessages = hasNext ? messages.subList(0, 20) : messages;

        return chatMessageDtoConverter.toGetPastChatMessagesResponse(resultMessages, nextCursor, hasNext);
    }

}
