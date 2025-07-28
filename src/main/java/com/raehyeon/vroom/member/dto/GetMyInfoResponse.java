package com.raehyeon.vroom.member.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMyInfoResponse {

    private String email;
    private String nickname;
    private ZonedDateTime createdAt;
    private long followerCount;
    private long followingCount;

}
