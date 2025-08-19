package com.raehyeon.vroom.security.jwt;

import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtBlacklistServiceImpl implements JwtBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    @Override
    public void addToken(String token) {
        Claims claims = jwtUtil.extractClaimsFromToken(token);

        Date now = new Date();
        Date expiration = claims.getExpiration();
        long ttl = expiration.getTime() - now.getTime();

        redisTemplate.opsForValue().set(token, "BLACKLISTED", ttl, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

}
