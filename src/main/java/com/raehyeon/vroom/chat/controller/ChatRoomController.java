package com.raehyeon.vroom.chat.controller;

import com.raehyeon.vroom.chat.dto.CreateChatRoomRequest;
import com.raehyeon.vroom.chat.dto.CreateChatRoomResponse;
import com.raehyeon.vroom.chat.dto.GetAllChatRoomsResponse;
import com.raehyeon.vroom.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping
    public Page<GetAllChatRoomsResponse> getAll(Pageable pageable) {
        return chatRoomService.getAllChatRooms(pageable);
    }

    @PostMapping
    public CreateChatRoomResponse create(@RequestBody CreateChatRoomRequest createChatRoomRequest) {
        return chatRoomService.createChatRoom(createChatRoomRequest);
    }

}
