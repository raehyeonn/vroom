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
        Member me = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow(MemberNotFoundException::new);
        Member member = memberRepository.findByNickname(followRequest.getNickname()).orElseThrow(MemberNotFoundException::new);

        if(followRepository.existsByFollowerAndFollowing(me, member)) {
            throw new AlreadyFollowingException();
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
        Member me = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow(MemberNotFoundException::new);
        Member member = memberRepository.findByNickname(unfollowRequest.getNickname()).orElseThrow(MemberNotFoundException::new);

        Follow follow = followRepository.findByFollowerAndFollowing(me, member).orElseThrow(FollowNotFoundException::new);
        followRepository.delete(follow);

        me.decreaseFollowingCount();
        member.decreaseFollowerCount();
    }

}
