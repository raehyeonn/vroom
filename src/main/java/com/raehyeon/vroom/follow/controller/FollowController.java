package com.raehyeon.vroom.follow.controller;

import com.raehyeon.vroom.follow.dto.UnfollowRequest;
import com.raehyeon.vroom.follow.dto.FollowRequest;
import com.raehyeon.vroom.follow.service.FollowActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowActionService followActionService;

    @PostMapping()
    public void follow(@AuthenticationPrincipal UserDetails userDetails, @RequestBody FollowRequest followRequest) {
        followActionService.executeFollow(userDetails, followRequest);
    }

    @PostMapping("/cancel")
    public void unfollow(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UnfollowRequest unfollowRequest) {
        followActionService.executeUnfollow(userDetails, unfollowRequest);
    }

}
