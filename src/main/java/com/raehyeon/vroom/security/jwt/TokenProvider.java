package com.raehyeon.vroom.security.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtUtil jwtUtil;

    private List<String> getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }

    public String createAccessToken(Authentication authentication) {
        return Jwts.builder()
            .setIssuer("vroom")
            .setSubject(authentication.getName())
            .claim("authorities", getAuthorities(authentication))
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtUtil.getAccessExp()))
            .setId(UUID.randomUUID().toString())
            .signWith(jwtUtil.createSigningKey(), SignatureAlgorithm.HS256)

            .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        return Jwts.builder()
            .setIssuer("vroom")
            .setSubject(authentication.getName())
            .claim("authorities", getAuthorities(authentication))
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtUtil.getRefreshExp()))
            .setId(UUID.randomUUID().toString())
            .signWith(jwtUtil.createSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }


}
