package com.raehyeon.vroom.chat.controller;

import com.raehyeon.vroom.chat.dto.UpdateChatRoomNameRequest;
import com.raehyeon.vroom.chat.dto.JoinChatRoomResponse;
import com.raehyeon.vroom.chat.dto.CreateChatRoomRequest;
import com.raehyeon.vroom.chat.dto.CreateChatRoomResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomSummaryResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomParticipantResponse;
import com.raehyeon.vroom.chat.dto.GetChatRoomDetailResponse;
import com.raehyeon.vroom.chat.dto.JoinChatRoomRequest;
import com.raehyeon.vroom.chat.dto.UpdateChatRoomNameResponse;
import com.raehyeon.vroom.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public CreateChatRoomResponse createChatRoom(@RequestBody CreateChatRoomRequest createChatRoomRequest) {
        return chatRoomService.createChatRoom(createChatRoomRequest);
    }

    @GetMapping
    public Page<GetChatRoomSummaryResponse> getChatRooms(Pageable pageable) {
        return chatRoomService.getChatRooms(pageable);
    }

    @GetMapping("/search")
    public GetChatRoomSummaryResponse getChatRoomByCode(@RequestParam String code) {
        return chatRoomService.getChatRoomByCode(code);
    }

    @GetMapping("/{chatRoomId}")
    public GetChatRoomDetailResponse getChatRoomDetail(@PathVariable Long chatRoomId) {
        return chatRoomService.getChatRoomDetail(chatRoomId);
    }

    @GetMapping("/{chatRoomId}/passwordRequired")
    public boolean passwordRequired(@PathVariable Long chatRoomId) {
        return chatRoomService.getChatRoomPasswordRequired(chatRoomId);
    }

    @PatchMapping("/{chatRoomId}/name")
    public UpdateChatRoomNameResponse updateChatRoomName(@PathVariable Long chatRoomId, @RequestBody UpdateChatRoomNameRequest changeRoomNameRequest) {
        return chatRoomService.updateChatRoomName(chatRoomId, changeRoomNameRequest);
    }

    @PostMapping("/{chatRoomId}/participants")
    public JoinChatRoomResponse joinChatRoom(@PathVariable Long chatRoomId, @AuthenticationPrincipal UserDetails userDetails, @RequestBody(required = false) JoinChatRoomRequest joinChatRoomRequest) {
        return chatRoomService.joinChatRoom(chatRoomId, userDetails, joinChatRoomRequest);
    }

    @DeleteMapping("/{chatRoomId}/participants")
    public void leaveChatRoom(@PathVariable Long chatRoomId, @AuthenticationPrincipal UserDetails userDetails) {
        chatRoomService.leaveChatRoom(chatRoomId, userDetails);
    }

    @GetMapping("/{chatRoomId}/participants")
    public Page<GetChatRoomParticipantResponse> getChatRoomParticipants(@PathVariable Long chatRoomId, Pageable pageable) {
        return chatRoomService.getChatRoomParticipants(chatRoomId, pageable);
    }

}
