package com.raehyeon.vroom.follow.converter;

import com.raehyeon.vroom.follow.dto.GetFollowerResponse;
import com.raehyeon.vroom.follow.dto.GetFollowingResponse;
import com.raehyeon.vroom.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class FollowDtoConverter {

    public GetFollowerResponse toGetFollowerResponse(Member follower, boolean isFollowedByMe) {
        return GetFollowerResponse.builder()
            .memberId(follower.getId())
            .nickname(follower.getNickname())
            .isFollowedByMe(isFollowedByMe)
            .build();
    }

    public GetFollowingResponse toGetFollowingResponse(Member following) {
        return GetFollowingResponse.builder()
            .memberId(following.getId())
            .nickname(following.getNickname())
            .build();
    }

}
