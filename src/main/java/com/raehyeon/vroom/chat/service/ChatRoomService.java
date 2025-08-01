package com.raehyeon.vroom.chat.service;

import com.raehyeon.vroom.chat.converter.ChatRoomDtoConverter;
import com.raehyeon.vroom.chat.converter.ChatRoomEntityConverter;
import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.domain.ChatRoomParticipant;
import com.raehyeon.vroom.chat.dto.ChangeRoomNameRequest;
import com.raehyeon.vroom.chat.dto.ChangeRoomNameResponse;
import com.raehyeon.vroom.chat.dto.ChatRoomEntryResponse;
import com.raehyeon.vroom.chat.dto.CreateChatRoomRequest;
import com.raehyeon.vroom.chat.dto.CreateChatRoomResponse;
import com.raehyeon.vroom.chat.dto.GetAllChatRoomsResponse;
import com.raehyeon.vroom.chat.dto.GetAllParticipantsResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomByCodeResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomDetailResponse;
import com.raehyeon.vroom.chat.dto.GetMyChatRoomListResponse;
import com.raehyeon.vroom.chat.dto.VerifyChatRoomPasswordRequest;
import com.raehyeon.vroom.chat.exception.ChatRoomNotFoundException;
import com.raehyeon.vroom.chat.exception.ChatRoomPasswordRequiredException;
import com.raehyeon.vroom.chat.exception.InvalidChatRoomPasswordException;
import com.raehyeon.vroom.chat.repository.ChatRoomParticipantRepository;
import com.raehyeon.vroom.chat.repository.ChatRoomRepository;
import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.exception.MemberNotFoundException;
import com.raehyeon.vroom.member.repository.MemberRepository;
import java.security.Principal;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final PasswordEncoder passwordEncoder;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomDtoConverter chatRoomDtoConverter;
    private final ChatRoomEntityConverter chatRoomEntityConverter;
    private final MemberRepository memberRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public Page<GetAllChatRoomsResponse> getAllChatRooms(Pageable pageable) {
        Page<ChatRoom> page = chatRoomRepository.findByHiddenFalse(pageable);

        return page.map(chatRoomDtoConverter::toGetAllChatRoomsResponse);
    }

    @Transactional
    public CreateChatRoomResponse createChatRoom(CreateChatRoomRequest createChatRoomRequest) {
        String code = RandomStringUtils.random(10, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        String encodedPassword = null;

        if(createChatRoomRequest.isPasswordRequired()) {
            String rawPassword = createChatRoomRequest.getPassword();

            if(rawPassword == null || rawPassword.isBlank()) {
                throw new ChatRoomPasswordRequiredException();
            }

            encodedPassword = passwordEncoder.encode(rawPassword);
        }

        ChatRoom chatRoom = chatRoomEntityConverter.toEntity(createChatRoomRequest, code, encodedPassword);
        chatRoomRepository.save(chatRoom);

        return chatRoomDtoConverter.toCreateChatRoomResponse(chatRoom); // 응답 반환
    }

    public GetChatRoomDetailResponse getById(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);

        return chatRoomDtoConverter.toGetChatRoomDetailResponse(chatRoom);
    }

    public GetChatRoomByCodeResponse getByCode(String chatRoomCode) {
        ChatRoom chatRoom = chatRoomRepository.findByCode(chatRoomCode).orElseThrow(ChatRoomNotFoundException::new);

        return chatRoomDtoConverter.toGetChatRoomByCodeResponse(chatRoom);
    }

    @Transactional
    public ChatRoomEntryResponse enterWithPasswordChatRoom(Long chatRoomId, VerifyChatRoomPasswordRequest verifyChatRoomPasswordRequest, Principal principal) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);

        if(chatRoom.isPasswordRequired()) {
            if(!passwordEncoder.matches(verifyChatRoomPasswordRequest.getPassword(), chatRoom.getPassword())) {
                throw new InvalidChatRoomPasswordException();
            }
        }

        Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(MemberNotFoundException::new);
        boolean exists = chatRoomParticipantRepository.existsByMemberAndChatRoom(member, chatRoom);

        if(!exists) {
            ChatRoomParticipant chatRoomParticipant = ChatRoomParticipant.builder()
                .member(member)
                .chatRoom(chatRoom)
                .enteredAt(ZonedDateTime.now())
                .build();
            chatRoomParticipantRepository.save(chatRoomParticipant);
        }

        return chatRoomDtoConverter.toChatRoomEntryResponse(true);
    }

    @Transactional
    public ChatRoomEntryResponse enterChatRoom(Long chatRoomId, Principal principal) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);
        Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(MemberNotFoundException::new);
        boolean exists = chatRoomParticipantRepository.existsByMemberAndChatRoom(member, chatRoom);

        if(!exists) {
            ChatRoomParticipant chatRoomParticipant = ChatRoomParticipant.builder()
                .member(member)
                .chatRoom(chatRoom)
                .enteredAt(ZonedDateTime.now())
                .build();
            chatRoomParticipantRepository.save(chatRoomParticipant);
        }

        return chatRoomDtoConverter.toChatRoomEntryResponse(true);
    }

    public boolean passwordRequired(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);

        return chatRoom.isPasswordRequired();
    }

    public Page<GetMyChatRoomListResponse> getMy(Principal principal, Pageable pageable) {
        Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(MemberNotFoundException::new);
        Page<ChatRoomParticipant> participantPage = chatRoomParticipantRepository.findAllByMember(member, pageable);

        return participantPage.map(chatRoomDtoConverter::toGetMyChatRoomListResponse);
    }

    @Transactional
    public void changeRoomName(Long chatRoomId, ChangeRoomNameRequest changeRoomNameRequest) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);
        chatRoom.changeName(changeRoomNameRequest.getRoomName());

        ChangeRoomNameResponse changeRoomNameResponse = chatRoomDtoConverter.toChangeRoomNameResponse(chatRoom);
        simpMessagingTemplate.convertAndSend("/sub/" + chatRoomId + "/info", changeRoomNameResponse);
    }

    @Transactional
    public void exit(Principal principal, Long chatRoomId) {
        Member member = memberRepository.findByEmail(principal.getName()).orElseThrow(MemberNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);
        ChatRoomParticipant chatRoomParticipant = chatRoomParticipantRepository.findByMemberAndChatRoom(member, chatRoom);

        chatRoomParticipantRepository.delete(chatRoomParticipant);
    }

    public Page<GetAllParticipantsResponse> getAllParticipants(Long chatRoomId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(ChatRoomNotFoundException::new);

        Page<ChatRoomParticipant> page = chatRoomParticipantRepository.findAllByChatRoom(chatRoom, pageable);
        return page.map(chatRoomDtoConverter::toGetAllParticipantsResponse);
    }

}
