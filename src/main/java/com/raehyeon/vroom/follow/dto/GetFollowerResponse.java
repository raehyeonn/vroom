package com.raehyeon.vroom.follow.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetFollowerResponse {

    private long memberId;
    private String nickname;
    private boolean isFollowedByMe;

}
