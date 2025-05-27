package com.raehyeon.vroom.chat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
import com.raehyeon.vroom.security.jwt.JwtUtil;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(ChatRoomController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatRoomService chatRoomService;

    @MockBean private JwtUtil jwtUtil;
    @MockBean private JwtAuthenticationService jwtAuthenticationService;
    @MockBean private CookieService cookieService;

    @Autowired
    private ObjectMapper objectMapper;

    private String fakeToken;

    @Test
    @DisplayName("채팅방 생성 성공")
    void createChatRoom_success() throws Exception {
        CreateChatRoomRequest request = CreateChatRoomRequest.builder()
            .name("방 이름")
            .build();

        CreateChatRoomResponse response = CreateChatRoomResponse.builder()
            .id(1L)
            .name("방 이름")
            .code("AbcdE12345")
            .createdAt(ZonedDateTime.now())
            .build();

        given(chatRoomService.createChatRoom(any(CreateChatRoomRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/chat-rooms")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("방 이름"));
    }

}

