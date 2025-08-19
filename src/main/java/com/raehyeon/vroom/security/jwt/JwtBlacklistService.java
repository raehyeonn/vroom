package com.raehyeon.vroom.security.jwt;

public interface JwtBlacklistService {

    void addToken(String token);
    boolean isBlacklisted(String token);

}
