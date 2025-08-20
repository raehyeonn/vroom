package com.raehyeon.vroom.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raehyeon.vroom.member.controller.MemberController;
import com.raehyeon.vroom.member.dto.CreateMemberRequest;
import com.raehyeon.vroom.member.dto.CreateMemberResponse;
import com.raehyeon.vroom.member.service.MemberService;
import com.raehyeon.vroom.security.jwt.CookieService;
import com.raehyeon.vroom.security.jwt.JwtAuthenticationService;
import com.raehyeon.vroom.security.jwt.JwtBlacklistService;
import com.raehyeon.vroom.security.jwt.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MemberControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private CookieService cookieService;
    @MockitoBean private JwtAuthenticationService jwtAuthenticationService;
    @MockitoBean private JwtBlacklistService jwtBlacklistService;
    @MockitoBean private MemberService memberService;

    @Test
    @DisplayName("회원 가입 - 성공")
    void createMemberSuccess() throws Exception {
        CreateMemberRequest createMemberRequest = CreateMemberRequest.builder()
            .email("test@example.com")
            .password("password1234!")
            .nickname("testMember")
            .build();

        CreateMemberResponse createMemberResponse = CreateMemberResponse.builder()
            .id(1L)
            .email("test@example.com")
            .nickname("testMember")
            .build();

        when(memberService.createMember(any(CreateMemberRequest.class))).thenReturn(createMemberResponse);

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMemberRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.nickname").value("testMember"));
    }

}
