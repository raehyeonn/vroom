package com.raehyeon.vroom.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.raehyeon.vroom.auth.converter.AuthDtoConverter;
import com.raehyeon.vroom.auth.dto.LoginRequest;
import com.raehyeon.vroom.auth.dto.LoginResponse;
import com.raehyeon.vroom.auth.service.AuthService;
import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.repository.MemberRepository;
import com.raehyeon.vroom.security.jwt.CookieService;
import com.raehyeon.vroom.security.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks private AuthService authService;

    @Mock private AuthenticationManager authenticationManager;
    @Mock private TokenProvider tokenProvider;
    @Mock private CookieService cookieService;
    @Mock private AuthDtoConverter authDtoConverter;
    @Mock private MemberRepository memberRepository;
    @Mock private HttpServletResponse httpServletResponse;

    @Test
    @DisplayName("로그인 시 토큰 발급 및 응답 반환")
    void createsTokensAndReturnsResponseOnLogin() {
        Member member = Member.builder()
            .id(1L)
            .email("test@example.com")
            .password("password1234!")
            .nickname("testMember")
            .memberRoles(new ArrayList<>())
            .createdAt(ZonedDateTime.now())
            .build();

        LoginRequest loginRequest = LoginRequest.builder()
            .email("test@example.com")
            .rawPassword("password1234!")
            .build();

        LoginResponse loginResponse = LoginResponse.builder()
            .memberId(member.getId())
            .email(member.getEmail())
            .build();

        Authentication mockAuth = mock(Authentication.class); // Authentication 타입의 가짜 객체
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
        when(mockAuth.getName()).thenReturn("test@example.com");

        String accessToken = "access.jwt.token";
        String refreshToken = "refresh.jwt.token";
        when(tokenProvider.createAccessToken(mockAuth)).thenReturn(accessToken);
        when(tokenProvider.createRefreshToken(mockAuth)).thenReturn(refreshToken);

        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));
        when(authDtoConverter.toLoginResponse(member.getId(), member.getEmail())).thenReturn(loginResponse);

        LoginResponse actualResponse = authService.login(loginRequest, httpServletResponse);

        assertThat(actualResponse).isEqualTo(loginResponse);

        verify(authenticationManager).authenticate(any());
        verify(tokenProvider).createAccessToken(mockAuth);
        verify(tokenProvider).createRefreshToken(mockAuth);
        verify(cookieService).addRefreshTokenCookie(httpServletResponse, refreshToken);
    }

}
