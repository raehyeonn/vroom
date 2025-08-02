package com.raehyeon.vroom.follow.converter;

import com.raehyeon.vroom.follow.domain.Follow;
import com.raehyeon.vroom.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class FollowEntityConverter {

    public Follow toEntity(Member currentMember, Member targetMember) {
        return Follow.builder()
            .follower(currentMember)
            .following(targetMember)
            .build();
    }

}
