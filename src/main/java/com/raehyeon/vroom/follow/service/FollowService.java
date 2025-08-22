package com.raehyeon.vroom.follow.service;

import com.raehyeon.vroom.follow.converter.FollowDtoConverter;
import com.raehyeon.vroom.follow.converter.FollowEntityConverter;
import com.raehyeon.vroom.follow.domain.Follow;
import com.raehyeon.vroom.follow.dto.GetFollowerResponse;
import com.raehyeon.vroom.follow.dto.GetFollowingResponse;
import com.raehyeon.vroom.follow.exception.AlreadyFollowingException;
import com.raehyeon.vroom.follow.exception.FollowNotFoundException;
import com.raehyeon.vroom.follow.repository.FollowRepository;
import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.exception.MemberNotFoundException;
import com.raehyeon.vroom.member.repository.MemberRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final FollowDtoConverter followDtoConverter;
    private final FollowEntityConverter followEntityConverter;

    @Transactional
    public void followMember(UserDetails userDetails, String targetNickname) {
        Member currentMember = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow(MemberNotFoundException::new);
        Member targetMember = memberRepository.findByNickname(targetNickname).orElseThrow(MemberNotFoundException::new);

        if (followRepository.existsByFollowerAndFollowing(currentMember, targetMember)) {
            throw new AlreadyFollowingException();
        }

        Follow follow = followEntityConverter.toEntity(currentMember, targetMember);
        followRepository.save(follow);

        currentMember.increaseFollowingCount();
        targetMember.increaseFollowerCount();
    }

    @Transactional
    public void unfollowMember(UserDetails userDetails, String targetNickname) {
        Member currentMember = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow(MemberNotFoundException::new);
        Member targetMember = memberRepository.findByNickname(targetNickname).orElseThrow(MemberNotFoundException::new);
        Follow follow = followRepository.findByFollowerAndFollowing(currentMember, targetMember).orElseThrow(FollowNotFoundException::new);
        followRepository.delete(follow);

        currentMember.decreaseFollowingCount();
        targetMember.decreaseFollowerCount();
    }

    public Page<GetFollowerResponse> getFollowers(UserDetails userDetails, Pageable pageable) {
        Member currentMember = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow(MemberNotFoundException::new);
        Page<Follow> followEntities = followRepository.findAllByFollowing(currentMember, pageable);

        List<Member> followers = followEntities.getContent().stream()
            .map(follow -> follow.getFollower())
            .toList();
        List<Follow> myFollowings = followRepository.findAllByFollowerAndFollowingIn(currentMember, followers);

        Set<Long> followingIds = myFollowings.stream()
            .map(follow -> follow.getFollowing().getId())
            .collect(Collectors.toSet());

        return followEntities.map(follow -> {
            Member follower = follow.getFollower();
            boolean isFollowedByMe = followingIds.contains(follower.getId());

            return followDtoConverter.toGetFollowerResponse(follower, isFollowedByMe);
        });
    }

    public Page<GetFollowingResponse> getFollowing(UserDetails userDetails, Pageable pageable) {
        Member currentMember = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow(MemberNotFoundException::new);
        Page<Follow> followEntities = followRepository.findAllByFollower(currentMember, pageable);

        return followEntities.map(follow -> {
            Member following = follow.getFollowing();

            return followDtoConverter.toGetFollowingResponse(following);
        });
    }

    @Transactional
    public void removeFollower(UserDetails userDetails, String targetNickname) {
        Member currentMember = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow(MemberNotFoundException::new);
        Member targetMember = memberRepository.findByNickname(targetNickname).orElseThrow(MemberNotFoundException::new);
        Follow follow = followRepository.findByFollowerAndFollowing(targetMember, currentMember).orElseThrow(FollowNotFoundException::new);
        followRepository.delete(follow);

        currentMember.decreaseFollowerCount();
        targetMember.decreaseFollowingCount();

        followRepository.findByFollowerAndFollowing(currentMember, targetMember).ifPresent(f -> {
            followRepository.delete(f);

            currentMember.decreaseFollowingCount();
            targetMember.decreaseFollowingCount();
        });
    }

}
