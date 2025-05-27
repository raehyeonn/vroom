package com.raehyeon.vroom.member.converter;

import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.dto.CreateMemberResponse;
import org.springframework.stereotype.Component;

@Component
public class MemberDtoConverter {

    public CreateMemberResponse toCreateMemberResponse(Member member) {
        return CreateMemberResponse.builder()
            .id(member.getId())
            .email(member.getEmail())
            .nickname(member.getNickname())
            .build();
    }

}
