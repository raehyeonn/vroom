package com.raehyeon.vroom.chat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raehyeon.vroom.chat.controller.ChatRoomController;
import com.raehyeon.vroom.chat.dto.CreateChatRoomRequest;
import com.raehyeon.vroom.chat.dto.CreateChatRoomResponse;
import com.raehyeon.vroom.chat.service.ChatRoomService;
import com.raehyeon.vroom.security.jwt.CookieService;
import com.raehyeon.vroom.security.jwt.JwtAuthenticationService;
import com.raehyeon.vroom.security.jwt.JwtBlacklistService;
import com.raehyeon.vroom.security.jwt.JwtUtil;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatRoomController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ChatRoomControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private CookieService cookieService;
    @MockitoBean private JwtAuthenticationService jwtAuthenticationService;
    @MockitoBean private JwtBlacklistService jwtBlacklistService;
    @MockitoBean private ChatRoomService chatRoomService;

    @Test
    @DisplayName("채팅방 생성 성공")
    void createChatRoomSuccess() throws Exception {
        CreateChatRoomRequest createChatRoomRequest = CreateChatRoomRequest.builder()
            .name("방 이름")
            .hidden(false)
            .passwordRequired(false)
            .password(null)
            .build();

        ZonedDateTime now = ZonedDateTime.now().truncatedTo(ChronoUnit.MILLIS); // 정밀도를 나노초에서 밀리초로 낮춘다.
        String expectedDateTimeString = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME); // now의 시간대 이름 제거

        CreateChatRoomResponse createChatRoomResponse = CreateChatRoomResponse.builder()
            .id(1L)
            .code("ABCDE23456")
            .name("test")
            .createdAt(now)
            .build();

        when(chatRoomService.createChatRoom(any(CreateChatRoomRequest.class))).thenReturn(createChatRoomResponse);

        mockMvc.perform(post("/api/chat-rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createChatRoomRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.code").value("ABCDE23456"))
            .andExpect(jsonPath("$.name").value("test"))
            .andExpect(jsonPath("$.createdAt").value(expectedDateTimeString));

        verify(chatRoomService).createChatRoom(any(CreateChatRoomRequest.class));
    }

}
