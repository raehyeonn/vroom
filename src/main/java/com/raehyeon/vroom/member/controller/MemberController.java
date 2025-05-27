package com.raehyeon.vroom.member.controller;

import com.raehyeon.vroom.member.dto.CreateMemberRequest;
import com.raehyeon.vroom.member.dto.CreateMemberResponse;
import com.raehyeon.vroom.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
