package com.raehyeon.vroom.member.converter;

import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.dto.CreateMemberRequest;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;

@Component
public class MemberEntityConverter {

    public Member toEntity(CreateMemberRequest createMemberRequest, String encodedPassword) {
        return Member.builder()
            .email(createMemberRequest.getEmail())
            .password(encodedPassword)
            .nickname(createMemberRequest.getNickname())
            .createdAt(ZonedDateTime.now())
            .build();
    }

}
