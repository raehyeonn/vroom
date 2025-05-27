package com.raehyeon.vroom.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtAuthenticationService jwtAuthenticationService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // "Bearer " 제거
                log.debug("Extracted token: {}", token);

                try {
                    Authentication auth = jwtAuthenticationService.getAuthentication(token);
                    log.debug("Authentication object created: {}", auth);

                    if (auth != null) {
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        accessor.setUser(auth); // 메시지에도 설정
                        log.debug("Authentication set in SecurityContextHolder");
                    } else {
                        log.warn("Authentication is null for token: {}", token);
                    }
                } catch (Exception e) {
                    log.error("Error during token authentication: {}", e.getMessage(), e);
                }
            }
        }

        return message;
    }

}
