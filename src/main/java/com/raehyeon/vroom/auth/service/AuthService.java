package com.raehyeon.vroom.auth.service;

import com.raehyeon.vroom.auth.converter.AuthDtoConverter;
import com.raehyeon.vroom.auth.dto.LoginRequest;
import com.raehyeon.vroom.auth.dto.LoginResponse;
import com.raehyeon.vroom.auth.exception.InvalidCredentialsException;
import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.repository.MemberRepository;
import com.raehyeon.vroom.security.jwt.CookieService;
import com.raehyeon.vroom.security.jwt.JwtBlacklistService;
import com.raehyeon.vroom.security.jwt.JwtUtil;
import com.raehyeon.vroom.security.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final CookieService cookieService;
    private final AuthDtoConverter authDtoConverter;
    private final MemberRepository memberRepository;
    private final JwtBlacklistService jwtBlacklistService;
    private final JwtUtil jwtUtil;

    private Authentication authenticateWithCredentials(LoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail();
            String rawPassword = loginRequest.getRawPassword();
            UsernamePasswordAuthenticationToken credentialsToken = new UsernamePasswordAuthenticationToken(email, rawPassword);

            return authenticationManager.authenticate(credentialsToken);
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }
    }

    private void setAuthTokensInResponse(HttpServletResponse httpServletResponse, String accessToken, String refreshToken) {
        httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);
        cookieService.addRefreshTokenCookie(httpServletResponse, refreshToken);
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        Authentication authentication = authenticateWithCredentials(loginRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);
        setAuthTokensInResponse(httpServletResponse, accessToken, refreshToken);

        String email = authentication.getName(); // 또는 loginRequest.getEmail()
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("사용자 없음"));

        return authDtoConverter.toLoginResponse(member.getId(), email);
    }

    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        String accessToken = jwtUtil.extractTokenFromRequestHeader(httpServletRequest);
        String refreshToken = cookieService.extractTokenFromCookie(httpServletRequest, "refresh_token");

        if(accessToken != null) {
            jwtBlacklistService.addToken(accessToken);
        }

        if (refreshToken != null) {
            jwtBlacklistService.addToken(refreshToken);
            cookieService.removeRefreshTokenCookie(httpServletResponse);
        }

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setHeader("Expires", "0");
    }

}
