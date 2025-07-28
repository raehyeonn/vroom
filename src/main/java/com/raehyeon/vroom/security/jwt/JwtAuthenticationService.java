package com.raehyeon.vroom.security.jwt;

import com.raehyeon.vroom.security.exception.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {

    private final JwtUtil jwtUtil;
    private final TokenProvider tokenProvider;

    public Authentication authenticateWithAccessToken(String token, HttpServletRequest httpServletRequest) {
        if(!jwtUtil.isTokenExpired(token)) {
            UserDetails userDetails = jwtUtil.extractUserDetailsFromToken(token); // 토큰에 들어있는 사용자정보 추출
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

            return authentication;
        } else {
            throw new TokenExpiredException();
        }
    }

    public Authentication refreshAccessToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        UserDetails userDetails = jwtUtil.extractUserDetailsFromToken(refreshToken);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String newAccessToken = tokenProvider.createAccessToken(authentication);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = jwtUtil.extractUserDetailsFromToken(token);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        return authentication;

    }

}
