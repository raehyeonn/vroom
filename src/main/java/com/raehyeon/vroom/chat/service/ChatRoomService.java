package com.raehyeon.vroom.chat.service;

import com.raehyeon.vroom.chat.converter.ChatRoomDtoConverter;
import com.raehyeon.vroom.chat.converter.ChatRoomEntityConverter;
import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.dto.CreateChatRoomRequest;
import com.raehyeon.vroom.chat.dto.CreateChatRoomResponse;
import com.raehyeon.vroom.chat.dto.GetAllChatRoomsResponse;
import com.raehyeon.vroom.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomDtoConverter chatRoomDtoConverter;
    private final ChatRoomEntityConverter chatRoomEntityConverter;

    public Page<GetAllChatRoomsResponse> getAllChatRooms(Pageable pageable) {
        Page<ChatRoom> page = chatRoomRepository.findAll(pageable);

        return page.map(chatRoomDtoConverter::toGetAllChatRoomsResponse);
    }

    @Transactional
    public CreateChatRoomResponse createChatRoom(CreateChatRoomRequest createChatRoomRequest) {
        String chatRoomCode = RandomStringUtils.randomAlphanumeric(10); // 영문자와 숫자에서 랜덤한 10자리 생성
        ChatRoom chatRoom = chatRoomEntityConverter.toEntity(createChatRoomRequest, chatRoomCode);
        chatRoomRepository.save(chatRoom);

        return chatRoomDtoConverter.toCreateChatRoomResponse(chatRoom); // 응답 반환
    }

}
