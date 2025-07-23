package com.raehyeon.vroom.follow.service;

import com.raehyeon.vroom.follow.domain.Follow;
import com.raehyeon.vroom.follow.dto.UnfollowRequest;
import com.raehyeon.vroom.follow.dto.FollowRequest;
import com.raehyeon.vroom.follow.exception.AlreadyFollowingException;
import com.raehyeon.vroom.follow.exception.FollowNotFoundException;
import com.raehyeon.vroom.follow.repository.FollowRepository;
import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.exception.MemberNotFoundException;
import com.raehyeon.vroom.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    @Transactional
    public void follow(UserDetails userDetails, FollowRequest followRequest) {
        Member me = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new MemberNotFoundException("존재하지 않거나 탈퇴한 사용자입니다."));
        Member member = memberRepository.findByNickname(followRequest.getNickname()).orElseThrow(() -> new MemberNotFoundException("존재하지 않거나 탈퇴한 사용자입니다."));

        if(followRepository.existsByFollowerAndFollowing(me, member)) {
            throw new AlreadyFollowingException("이미 팔로우한 사용자입니다.");
        }

        Follow follow = Follow.builder()
            .follower(me)
            .following(member)
            .build();

        followRepository.save(follow);

        me.increaseFollowingCount();
        member.increaseFollowerCount();
    }

    @Transactional
    public void unfollow(UserDetails userDetails, UnfollowRequest unfollowRequest) {
        Member me = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new MemberNotFoundException("존재하지 않거나 탈퇴한 사용자입니다."));
        Member member = memberRepository.findByNickname(unfollowRequest.getNickname()).orElseThrow(() -> new MemberNotFoundException("존재하지 않거나 탈퇴한 사용자입니다."));

        Follow follow = followRepository.findByFollowerAndFollowing(me, member).orElseThrow(() -> new FollowNotFoundException("팔로우 관계가 존재하지 않습니다."));
        followRepository.delete(follow);

        me.decreaseFollowingCount();
        member.decreaseFollowerCount();
    }

}
