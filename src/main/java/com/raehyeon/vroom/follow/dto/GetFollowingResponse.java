package com.raehyeon.vroom.follow.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetFollowingResponse {

    private long memberId;
    private String nickname;

}
