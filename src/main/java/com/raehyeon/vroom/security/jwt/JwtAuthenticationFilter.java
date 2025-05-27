package com.raehyeon.vroom.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CookieService cookieService;
    private final JwtAuthenticationService jwtAuthenticationService;

    private static final List<String> PUBLIC_PATHS = Arrays.asList("/api/auth/login");

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.extractTokenFromRequestHeader(httpServletRequest);
        String refreshToken = cookieService.extractTokenFromCookie(httpServletRequest, "refresh_token");

        log.info("AccessToken: {}", accessToken);  // 여기서 토큰 값 확인
        log.info("RefreshToken: {}", refreshToken);  // 리프레시 토큰 값 확인

        Authentication authentication = null; // 인증 과정 진행 전 현재는 인증된 사용자 정보가 없음을 명확하게 나타낸다.
        boolean tokenRefreshed = false; // 토큰 갱신 여부 확인

        // 인증 토큰이 존재하는 경우
        if(accessToken != null) {
            try {
                authentication = jwtAuthenticationService.authenticateWithAccessToken(accessToken, httpServletRequest);
            } catch (ExpiredJwtException e) {
                accessToken = null;
            }
        }

        // 리프레시 토큰은 존재하지만 인증된 사용자 정보가 없는 경우. 즉, 인증 토큰 기간이 만료된 경우 여기로 넘어옴.
        if(authentication == null && refreshToken != null) {
            authentication = jwtAuthenticationService.refreshAccessToken(refreshToken, httpServletRequest, httpServletResponse);
            accessToken = jwtUtil.extractTokenFromResponseHeader(httpServletResponse);
            tokenRefreshed = true;
        }

        if(authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Authenticated user: {}", authentication.getName());  // 인증된 사용자 이름
            log.info("Authentication details: {}", authentication);

            if(tokenRefreshed) {
                httpServletRequest = new CustomHttpServletRequestWrapper(httpServletRequest);
                ((CustomHttpServletRequestWrapper)httpServletRequest).putHeader("Authorization", "Bearer " + accessToken);
            }

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.getWriter().write("Authentication failed. Please log in again.");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest httpServletRequest) {
        String path = httpServletRequest.getRequestURI();
        String method = httpServletRequest.getMethod();

        boolean shouldSkip = PUBLIC_PATHS.stream().anyMatch(path::equals)
            || path.equals("/")
            || ("POST".equalsIgnoreCase(method) && path.equals("/api/auth/login"))
            || ("POST".equalsIgnoreCase(method) && path.equals("/api/members"))
            || ("GET".equalsIgnoreCase(method) && path.equals("/api/chat-rooms"));
        return shouldSkip;
    }
}
