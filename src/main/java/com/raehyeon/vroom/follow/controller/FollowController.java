package com.raehyeon.vroom.follow.controller;

import com.raehyeon.vroom.follow.dto.GetFollowerResponse;
import com.raehyeon.vroom.follow.dto.GetFollowingResponse;
import com.raehyeon.vroom.follow.service.FollowActionService;
import com.raehyeon.vroom.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class FollowController {

    private final FollowActionService followActionService;
    private final FollowService followService;

    @PostMapping("/{targetNickname}/follow")
    public void followMember(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String targetNickname) {
        followActionService.executeFollowMember(userDetails, targetNickname);
    }

    @DeleteMapping("/{targetNickname}/follow")
    public void unfollowMember(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String targetNickname) {
        followActionService.executeUnfollowMember(userDetails, targetNickname);
    }

    @GetMapping("/me/followers")
    public Page<GetFollowerResponse> getFollowers(@AuthenticationPrincipal UserDetails userDetails, Pageable pageable) {
        return followService.getFollowers(userDetails, pageable);
    }

    @GetMapping("/me/following")
    public Page<GetFollowingResponse> getFollowing(@AuthenticationPrincipal UserDetails userDetails, Pageable pageable) {
        return followService.getFollowing(userDetails, pageable);
    }

    @DeleteMapping("/me/followers/{targetNickname}")
    public void removeFollower(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String targetNickname) {
        followActionService.executeRemoveFollower(userDetails, targetNickname);
    }

}
