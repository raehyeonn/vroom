package com.raehyeon.vroom.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateMemberResponse {

    private Long id;
    private String email;
    private String nickname;

}
