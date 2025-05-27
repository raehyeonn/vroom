package com.raehyeon.vroom.chat.service;

import com.raehyeon.vroom.chat.converter.ChatMessageDtoConverter;
import com.raehyeon.vroom.chat.converter.ChatMessageEntityConverter;
import com.raehyeon.vroom.chat.domain.ChatMessage;
import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.dto.SendChatMessageRequest;
import com.raehyeon.vroom.chat.dto.SendChatMessageResponse;
import com.raehyeon.vroom.chat.exception.ChatRoomNotFoundException;
import com.raehyeon.vroom.chat.repository.ChatMessageRepository;
import com.raehyeon.vroom.chat.repository.ChatRoomRepository;
import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.exception.MemberNotFoundException;
import com.raehyeon.vroom.member.repository.MemberRepository;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
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

}
