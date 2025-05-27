package com.raehyeon.vroom.auth.converter;

import com.raehyeon.vroom.auth.dto.LoginResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthDtoConverter {

    public LoginResponse toLoginResponse(Long id, String email) {
        return LoginResponse.builder()
            .memberId(id)
            .email(email)
            .build();
    }

}
