package com.raehyeon.vroom.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raehyeon.vroom.auth.controller.AuthController;
import com.raehyeon.vroom.auth.dto.LoginRequest;
import com.raehyeon.vroom.auth.dto.LoginResponse;
import com.raehyeon.vroom.auth.service.AuthService;
import com.raehyeon.vroom.security.jwt.CookieService;
import com.raehyeon.vroom.security.jwt.JwtAuthenticationService;
import com.raehyeon.vroom.security.jwt.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private JwtUtil jwtUtil;
    @MockBean private JwtAuthenticationService jwtAuthenticationService;
    @MockBean private CookieService cookieService;

    @Test
    @DisplayName("로그인 성공 시 응답 반환")
    void loginSuccess() throws Exception {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
            .email("test@example.com")
            .rawPassword("password1!")
            .build();

        LoginResponse loginResponse = LoginResponse.builder()
            .memberId(1L)
            .email("test@example.com")
            .build();

        when(authService.login(any(), any())).thenReturn(loginResponse);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.memberId").value(1L))
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }

}
