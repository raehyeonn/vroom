package com.raehyeon.vroom.member.controller;

import com.raehyeon.vroom.member.dto.ChangeNicknameRequest;
import com.raehyeon.vroom.member.dto.CreateMemberRequest;
import com.raehyeon.vroom.member.dto.CreateMemberResponse;
import com.raehyeon.vroom.member.dto.GetMemberBySearchResponse;
import com.raehyeon.vroom.member.dto.GetMyInfoResponse;
import com.raehyeon.vroom.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public CreateMemberResponse create(@RequestBody CreateMemberRequest createMemberRequest) {
        return memberService.createMember(createMemberRequest);
    }

    @GetMapping("/me")
    public GetMyInfoResponse getMine(@AuthenticationPrincipal UserDetails userDetails) {
        return memberService.getMine(userDetails);
    }

    @PostMapping("/nickname")
    public void changeNickname(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid ChangeNicknameRequest changeNicknameRequest) {
        memberService.changeNickname(userDetails, changeNicknameRequest);
    }

    @GetMapping("/search")
    public GetMemberBySearchResponse searchMember(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String nickname) {
        return memberService.searchMember(userDetails, nickname);
    }

}
