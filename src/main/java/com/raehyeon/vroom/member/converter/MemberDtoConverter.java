package com.raehyeon.vroom.member.converter;

import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.dto.CreateMemberResponse;
import com.raehyeon.vroom.member.dto.GetMemberBySearchResponse;
import com.raehyeon.vroom.member.dto.GetMyInfoResponse;
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

    public GetMyInfoResponse toGetMyInfoResponse(Member member) {
        return GetMyInfoResponse.builder()
            .email(member.getEmail())
            .nickname(member.getNickname())
            .followerCount(member.getFollowerCount())
            .followingCount(member.getFollowingCount())
            .createdAt(member.getCreatedAt())
            .build();
    }

    public GetMemberBySearchResponse toGetMemberBySearchResponse(Member member, boolean isFollowing) {
        return GetMemberBySearchResponse.builder()
            .nickname(member.getNickname())
            .isFollowing(isFollowing)
            .build();
    }

}
