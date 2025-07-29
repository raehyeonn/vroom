package com.raehyeon.vroom.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMemberBySearchResponse {

    private String nickname;

    @JsonProperty("isFollowing")
    private boolean isFollowing;

}
