package com.raehyeon.vroom.chat.service;

import com.raehyeon.vroom.chat.converter.ChatRoomDtoConverter;
import com.raehyeon.vroom.chat.converter.ChatRoomEntityConverter;
import com.raehyeon.vroom.chat.converter.ChatRoomParticipantEntityConverter;
import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.domain.ChatRoomParticipant;
import com.raehyeon.vroom.chat.dto.UpdateChatRoomNameRequest;
import com.raehyeon.vroom.chat.dto.UpdateChatRoomNameResponse;
import com.raehyeon.vroom.chat.dto.JoinChatRoomResponse;
import com.raehyeon.vroom.chat.dto.CreateChatRoomRequest;
import com.raehyeon.vroom.chat.dto.CreateChatRoomResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomSummaryResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomParticipantResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomDetailResponse;
import com.raehyeon.vroom.chat.dto.JoinChatRoomRequest;
import com.raehyeon.vroom.chat.exception.ChatRoomNotFoundException;
import com.raehyeon.vroom.chat.exception.ChatRoomParticipantNotFoundException;
import com.raehyeon.vroom.chat.exception.ChatRoomPasswordRequiredException;
import com.raehyeon.vroom.chat.exception.InvalidChatRoomPasswordException;
import com.raehyeon.vroom.chat.repository.ChatRoomParticipantRepository;
import com.raehyeon.vroom.chat.repository.ChatRoomRepository;
import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.exception.MemberNotFoundException;
import com.raehyeon.vroom.member.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final PasswordEncoder passwordEncoder;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomDtoConverter chatRoomDtoConverter;
    private final ChatRoomEntityConverter chatRoomEntityConverter;
    private final ChatRoomParticipantEntityConverter chatRoomParticipantEntity;
    private final MemberRepository memberRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public CreateChatRoomResponse createChatRoom(CreateChatRoomRequest createChatRoomRequest) {
        String code = RandomStringUtils.random(10, "ABCDEFGHJKLMNPQRSTUVWXYZ23456789");
        String encodedPassword = null;

        if(createChatRoomRequest.isPasswordRequired()) {

            log.info("비밀번호 확인 중: {}", createChatRoomRequest.getPassword());
            String rawPassword = createChatRoomRequest.getPassword();

            if(rawPassword == null || rawPassword.isBlank()) {
                throw new ChatRoomPasswordRequiredException();
            }

            encodedPassword = passwordEncoder.encode(rawPassword);
        }

        ChatRoom chatRoom = chatRoomEntityConverter.toEntity(createChatRoomRequest, code, encodedPassword);
        chatRoomRepository.save(chatRoom);

        return chatRoomDtoConverter.toCreateChatRoomResponse(chatRoom);
    }

    public Page<GetChatRoomSummaryResponse> getChatRooms(Pageable pageable) {
        Page<ChatRoom> chatRooms = chatRoomRepository.findByHiddenFalse(pageable);

        return chatRooms.map(chatRoomDtoConverter::toGetChatRoomSummaryResponse);
    }

    public GetChatRoomSummaryResponse getChatRoomByCode(String code) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findByCode(code);

        return chatRoom.map(chatRoomDtoConverter::toGetChatRoomSummaryResponse).orElse(null);
    }

    public GetChatRoomDetailResponse getChatRoomDetail(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);

        return chatRoomDtoConverter.toGetChatRoomDetailResponse(chatRoom);
    }

    public boolean getChatRoomPasswordRequired(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);

        return chatRoom.isPasswordRequired();
    }

    @Transactional
    public UpdateChatRoomNameResponse updateChatRoomName(Long chatRoomId, UpdateChatRoomNameRequest changeRoomNameRequest) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);
        chatRoom.changeName(changeRoomNameRequest.getName());

        UpdateChatRoomNameResponse updateChatRoomNameResponse = chatRoomDtoConverter.toUpdateChatRoomNameResponse(chatRoom);
        simpMessagingTemplate.convertAndSend("/sub/" + chatRoomId + "/info", updateChatRoomNameResponse);

        return updateChatRoomNameResponse;
    }

    @Transactional
    public JoinChatRoomResponse joinChatRoom(Long chatRoomId, UserDetails userDetails, JoinChatRoomRequest joinChatRoomRequest) {

        log.info("🔐 joinChatRoom called with chatRoomId={}, userEmail={}, request={}",
            chatRoomId,
            userDetails.getUsername(),
            joinChatRoomRequest);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);

        if(chatRoom.isPasswordRequired()) {

            if(joinChatRoomRequest == null || !passwordEncoder.matches(joinChatRoomRequest.getPassword(), chatRoom.getPassword())) {
                throw new InvalidChatRoomPasswordException();
            }
        }

        Member member = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow(MemberNotFoundException::new);
        boolean isParticipant = chatRoomParticipantRepository.existsByMemberAndChatRoom(member, chatRoom);

        if(!isParticipant) {
            ChatRoomParticipant chatRoomParticipant = chatRoomParticipantEntity.toEntity(member, chatRoom);
            chatRoomParticipantRepository.save(chatRoomParticipant);
        }

        return chatRoomDtoConverter.toJoinChatRoomResponse(true);
    }

    @Transactional
    public void leaveChatRoom(Long chatRoomId, UserDetails userDetails) {
        Member member = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow(MemberNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);
        ChatRoomParticipant chatRoomParticipant = chatRoomParticipantRepository.findByMemberAndChatRoom(member, chatRoom).orElseThrow(ChatRoomParticipantNotFoundException::new);
        chatRoomParticipantRepository.delete(chatRoomParticipant);
    }

    public Page<GetChatRoomParticipantResponse> getChatRoomParticipants(Long chatRoomId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);
        Page<ChatRoomParticipant> chatRoomParticipants = chatRoomParticipantRepository.findAllByChatRoom(chatRoom, pageable);

        return chatRoomParticipants.map(chatRoomDtoConverter::toGetChatRoomParticipantResponse);
    }

}
