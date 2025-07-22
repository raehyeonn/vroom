package com.raehyeon.vroom.chat.controller;

import com.raehyeon.vroom.chat.dto.ChangeRoomNameRequest;
import com.raehyeon.vroom.chat.dto.ChatRoomEntryResponse;
import com.raehyeon.vroom.chat.dto.CreateChatRoomRequest;
import com.raehyeon.vroom.chat.dto.CreateChatRoomResponse;
import com.raehyeon.vroom.chat.dto.GetAllChatRoomsResponse;
import com.raehyeon.vroom.chat.dto.GetAllParticipantsResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomByCodeResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomDetailResponse;
import com.raehyeon.vroom.chat.dto.GetMyChatRoomListResponse;
import com.raehyeon.vroom.chat.dto.VerifyChatRoomPasswordRequest;
import com.raehyeon.vroom.chat.service.ChatRoomService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{chatRoomId}")
    public GetChatRoomDetailResponse getById(@PathVariable Long chatRoomId) {
        return chatRoomService.getById(chatRoomId);
    }

    @GetMapping("/by-code/{chatRoomCode}")
    public GetChatRoomByCodeResponse getByCode(@PathVariable String chatRoomCode) {
        return chatRoomService.getByCode(chatRoomCode);
    }

    @PostMapping("/{chatRoomId}/enter-with-password")
    public ChatRoomEntryResponse enterWithPassword(@PathVariable Long chatRoomId, @RequestBody VerifyChatRoomPasswordRequest verifyChatRoomPasswordRequest, Principal principal) {
        return chatRoomService.enterWithPasswordChatRoom(chatRoomId, verifyChatRoomPasswordRequest, principal);
    }

    @PostMapping("/{chatRoomId}/enter")
    public ChatRoomEntryResponse enter(@PathVariable Long chatRoomId, Principal principal) {
        return chatRoomService.enterChatRoom(chatRoomId, principal);
    }

    @GetMapping("/{chatRoomId}/passwordRequired")
    public boolean passwordRequired(@PathVariable Long chatRoomId) {
        return chatRoomService.passwordRequired(chatRoomId);
    }

    @GetMapping("/me")
    public Page<GetMyChatRoomListResponse> getMy(Principal principal, Pageable pageable) {
        return chatRoomService.getMy(principal, pageable);
    }

    @PostMapping("/{chatRoomId}/change-name")
    public void changeRoomName(@PathVariable Long chatRoomId, @RequestBody ChangeRoomNameRequest changeRoomNameRequest) {
        chatRoomService.changeRoomName(chatRoomId, changeRoomNameRequest);
    }

    @PostMapping("/{chatRoomId}/exit")
    public void exit(Principal principal, @PathVariable Long chatRoomId) {
        chatRoomService.exit(principal, chatRoomId);
    }

    @GetMapping("/{chatRoomId}/participants")
    public Page<GetAllParticipantsResponse> getAllParticipants(@PathVariable Long chatRoomId, Pageable pageable) {
        return chatRoomService.getAllParticipants(chatRoomId, pageable);
    }

}
