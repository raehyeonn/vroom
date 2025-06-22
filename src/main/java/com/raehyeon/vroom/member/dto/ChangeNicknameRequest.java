package com.raehyeon.vroom.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangeNicknameRequest {

    private String nickname;

}
