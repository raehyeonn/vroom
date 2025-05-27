package com.raehyeon.vroom.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private Long memberId;
    private String email;

}
