package com.raehyeon.vroom.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.NonNull;
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
    protected void doFilterInternal(@NonNull HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.extractTokenFromRequestHeader(httpServletRequest);
        String refreshToken = cookieService.extractTokenFromCookie(httpServletRequest, "refresh_token");

        log.info("AccessToken: {}", accessToken);  // 여기서 토큰 값 확인
        log.info("RefreshToken: {}", refreshToken);  // 리프레시 토큰 값 확인

        Authentication authentication = null; // 인증 과정 진행 전 현재는 인증된 사용자 정보가 없음을 명확하게 나타낸다.
        boolean tokenRefreshed = false; // 토큰 갱신 여부 확인

        // 인증 토큰이 존재하는 경우
        if(accessToken != null) {
            try {
                log.info("[필터] accessToken 존재 → 인증 시도");
                authentication = jwtAuthenticationService.authenticateWithAccessToken(accessToken, httpServletRequest);
                log.info("[필터] accessToken 인증 성공: {}", authentication != null);
            } catch (ExpiredJwtException e) {
                log.warn("[필터] accessToken 만료됨");
                accessToken = null;
            } catch (Exception e) {
                log.error("[필터] accessToken 인증 중 예외 발생: {}", e.getMessage());
            }
        }

        // 리프레시 토큰은 존재하지만 인증된 사용자 정보가 없는 경우. 즉, 인증 토큰 기간이 만료된 경우 여기로 넘어옴.
        if(authentication == null && refreshToken != null) {
            log.info("[필터] accessToken 없음, refreshToken 사용해서 토큰 갱신 시도");
            authentication = jwtAuthenticationService.refreshAccessToken(refreshToken, httpServletRequest, httpServletResponse);
            accessToken = jwtUtil.extractTokenFromResponseHeader(httpServletResponse);
            tokenRefreshed = true;
            log.info("[필터] 토큰 갱신 결과 authentication: {}", authentication != null);
        }

        if(authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[필터] 인증된 사용자: {}", authentication.getName());
            log.info("[필터] 권한 목록:");
            authentication.getAuthorities().forEach(auth -> log.info(" - {}", auth.getAuthority()));

            if(tokenRefreshed) {
                httpServletRequest = new CustomHttpServletRequestWrapper(httpServletRequest);
                ((CustomHttpServletRequestWrapper)httpServletRequest).putHeader("Authorization", "Bearer " + accessToken);
            }

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            log.warn("[필터] 인증 실패 - 401 반환");
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
