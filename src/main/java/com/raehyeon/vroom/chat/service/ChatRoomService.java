package com.raehyeon.vroom.chat.service;

import com.raehyeon.vroom.chat.converter.ChatRoomDtoConverter;
import com.raehyeon.vroom.chat.converter.ChatRoomEntityConverter;
import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.dto.CreateChatRoomRequest;
import com.raehyeon.vroom.chat.dto.CreateChatRoomResponse;
import com.raehyeon.vroom.chat.dto.GetAllChatRoomsResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomByCodeResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomDetailResponse;
import com.raehyeon.vroom.chat.dto.VerifyChatRoomPasswordRequest;
import com.raehyeon.vroom.chat.exception.ChatRoomNotFoundException;
import com.raehyeon.vroom.chat.exception.ChatRoomPasswordRequiredException;
import com.raehyeon.vroom.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                throw new ChatRoomPasswordRequiredException("채팅방 비밀번호가 필요합니다.");
            }

            encodedPassword = passwordEncoder.encode(rawPassword);
        }

        ChatRoom chatRoom = chatRoomEntityConverter.toEntity(createChatRoomRequest, code, encodedPassword);
        chatRoomRepository.save(chatRoom);

        return chatRoomDtoConverter.toCreateChatRoomResponse(chatRoom); // 응답 반환
    }

    public GetChatRoomDetailResponse getById(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ChatRoomNotFoundException("존재하지 않거나 삭제된 채팅방입니다."));

        return chatRoomDtoConverter.toGetChatRoomDetailResponse(chatRoom);
    }

    public GetChatRoomByCodeResponse getByCode(String chatRoomCode) {
        ChatRoom chatRoom = chatRoomRepository.findByCode(chatRoomCode).orElseThrow(() -> new ChatRoomNotFoundException("존재하지 않거나 삭제된 채팅방입니다."));

        return chatRoomDtoConverter.toGetChatRoomByCodeResponse(chatRoom);
    }

    public boolean enterChatRoom(Long chatRoomId, VerifyChatRoomPasswordRequest verifyChatRoomPasswordRequest) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ChatRoomNotFoundException("존재하지 않거나 삭제된 채팅방입니다."));

        return passwordEncoder.matches(verifyChatRoomPasswordRequest.getPassword(), chatRoom.getPassword());
    }

}
