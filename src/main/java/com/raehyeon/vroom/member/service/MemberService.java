package com.raehyeon.vroom.member.service;

import com.raehyeon.vroom.follow.repository.FollowRepository;
import com.raehyeon.vroom.member.converter.MemberEntityConverter;
import com.raehyeon.vroom.member.converter.MemberDtoConverter;
import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.dto.ChangeNicknameRequest;
import com.raehyeon.vroom.member.dto.CreateMemberRequest;
import com.raehyeon.vroom.member.dto.CreateMemberResponse;
import com.raehyeon.vroom.member.dto.GetMemberBySearchResponse;
import com.raehyeon.vroom.member.dto.GetMyInfoResponse;
import com.raehyeon.vroom.member.exception.DuplicateEmailException;
import com.raehyeon.vroom.member.exception.DuplicateNicknameException;
import com.raehyeon.vroom.member.exception.MemberNotFoundException;
import com.raehyeon.vroom.member.repository.MemberRepository;
import com.raehyeon.vroom.role.domain.MemberRole;
import com.raehyeon.vroom.role.domain.Role;
import com.raehyeon.vroom.role.domain.RoleType;
import com.raehyeon.vroom.role.repository.MemberRoleRepository;
import com.raehyeon.vroom.role.repository.RoleRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final RoleRepository roleRepository;
    private final MemberEntityConverter memberEntityConverter;
    private final MemberDtoConverter memberDtoConverter;
    private final FollowRepository followRepository;

    @Transactional
    public CreateMemberResponse createMember(CreateMemberRequest createMemberRequest) {
        if(memberRepository.existsByEmail(createMemberRequest.getEmail())) {
            throw new DuplicateEmailException();
        }

        if(memberRepository.existsByNickname(createMemberRequest.getNickname())) {
            throw new DuplicateNicknameException();
        }

        String encodedPassword = passwordEncoder.encode(createMemberRequest.getPassword());
        Member member = memberEntityConverter.toEntity(createMemberRequest, encodedPassword);

        Role role = roleRepository.findByRoleType(RoleType.MEMBER).orElseThrow(() -> new IllegalStateException("기본 MEMBER 역할이 존재하지 않습니다."));

        MemberRole memberRole = MemberRole.create(member, role);

        member.getMemberRoles().add(memberRole);
        memberRepository.save(member);
        memberRoleRepository.save(memberRole);


        return memberDtoConverter.toCreateMemberResponse(member);
    }

    public GetMyInfoResponse getMine(UserDetails userDetails) {
        Member member = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow(MemberNotFoundException::new);

        return memberDtoConverter.toGetMyInfoResponse(member);
    }

    @Transactional
    public void changeNickname(UserDetails userDetails, ChangeNicknameRequest changeNicknameRequest) {
        Member member = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow(MemberNotFoundException::new);
        String nickname = changeNicknameRequest.getNickname();

        if(memberRepository.existsByNickname(nickname)) {
            throw new DuplicateNicknameException();
        }

        member.changeNickname(nickname);
    }

    public GetMemberBySearchResponse searchMember(UserDetails userDetails, String nickname) {
        Member me = memberRepository.findByEmail(userDetails.getUsername()).orElseThrow(MemberNotFoundException::new);
        Optional<Member> member = memberRepository.findByNickname(nickname);

        if(member.isPresent()) {
            boolean isFollowing = followRepository.existsByFollowerAndFollowing(me, member.get());

            return memberDtoConverter.toGetMemberBySearchResponse(member.get(), isFollowing);
        } else {
            return null;
        }

    }

}
