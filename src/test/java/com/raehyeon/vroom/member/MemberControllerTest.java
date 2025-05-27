package com.raehyeon.vroom.member;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raehyeon.vroom.member.controller.MemberController;
import com.raehyeon.vroom.member.dto.CreateMemberRequest;
import com.raehyeon.vroom.member.dto.CreateMemberResponse;
import com.raehyeon.vroom.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 가입 - 성공")
    void createMember_success() throws Exception {
        // given
        CreateMemberRequest request = CreateMemberRequest.builder()
            .email("test@example.com")
            .password("password1!")
            .nickname("tester")
            .build();

        CreateMemberResponse response = CreateMemberResponse.builder()
            .id(1L)
            .email("test@example.com")
            .nickname("tester")
            .build();

        when(memberService.createMember(Mockito.any(CreateMemberRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.nickname").value("tester"));
    }
}
