package com.raehyeon.vroom.member;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.raehyeon.vroom.member.converter.MemberDtoConverter;
import com.raehyeon.vroom.member.converter.MemberEntityConverter;
import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.dto.CreateMemberRequest;
import com.raehyeon.vroom.member.dto.CreateMemberResponse;
import com.raehyeon.vroom.member.exception.DuplicateEmailException;
import com.raehyeon.vroom.member.exception.DuplicateNicknameException;
import com.raehyeon.vroom.member.repository.MemberRepository;
import com.raehyeon.vroom.member.service.MemberService;
import com.raehyeon.vroom.role.domain.MemberRole;
import com.raehyeon.vroom.role.domain.Role;
import com.raehyeon.vroom.role.domain.RoleType;
import com.raehyeon.vroom.role.repository.MemberRoleRepository;
import com.raehyeon.vroom.role.repository.RoleRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks private MemberService memberService;

    @Mock private PasswordEncoder passwordEncoder;
    @Mock private MemberRepository memberRepository;
    @Mock private MemberRoleRepository memberRoleRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private MemberEntityConverter memberEntityConverter;
    @Mock private MemberDtoConverter memberDtoConverter;

    private CreateMemberRequest createMemberRequest;
    private Member member;
    private Role role;

    @BeforeEach
    void setUp() {
        createMemberRequest = CreateMemberRequest.builder()
            .email("test@example.com")
            .password("password1234!")
            .nickname("testMember")
            .build();

        member = Member.builder()
            .id(1L)
            .email("test@example.com")
            .password("password1234!")
            .nickname("testMember")
            .memberRoles(new ArrayList<>())
            .createdAt(ZonedDateTime.now())
            .build();

        role = Role.builder()
            .roleType(RoleType.MEMBER)
            .build();

        MemberRole memberRole = MemberRole.create(member, role);
        member.getMemberRoles().add(memberRole);
    }

    @Test
    @DisplayName("회원 가입 - 이메일 중복 예외")
    void signUpFailsWhenEmailIsDuplicate() {
        when(memberRepository.existsByEmail(createMemberRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> memberService.createMember(createMemberRequest))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessage("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("회원 가입 - 닉네임 중복 예외")
    void signUpFailsWhenNicknameIsDuplicate() {
        when(memberRepository.existsByNickname(createMemberRequest.getNickname())).thenReturn(true);

        assertThatThrownBy(() -> memberService.createMember(createMemberRequest))
            .isInstanceOf(DuplicateNicknameException.class)
            .hasMessage("이미 사용 중인 닉네임입니다.");
    }

    @Test
    @DisplayName("회원 가입 - 성공")
    void signUpSucceeds() {
        String encodedPassword = "encodedPassword";

        when(memberRepository.existsByEmail(createMemberRequest.getEmail())).thenReturn(false);
        when(memberRepository.existsByNickname(createMemberRequest.getNickname())).thenReturn(false);

        when(passwordEncoder.encode(createMemberRequest.getPassword())).thenReturn(encodedPassword);
        when(memberEntityConverter.toEntity(createMemberRequest, encodedPassword)).thenReturn(member);

        when(roleRepository.findByRoleType(RoleType.MEMBER)).thenReturn(Optional.of(role));
        when(memberDtoConverter.toCreateMemberResponse(member)).thenReturn(
            CreateMemberResponse.builder()
                .id(1L)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build()
        );

        CreateMemberResponse actualResponse = memberService.createMember(createMemberRequest);

        assertEquals(member.getEmail(), actualResponse.getEmail());
        assertEquals(member.getNickname(), actualResponse.getNickname());

        verify(memberRepository).save(member);
        verify(memberRoleRepository).save(any(MemberRole.class));
        verify(passwordEncoder).encode(createMemberRequest.getPassword());
        verify(memberEntityConverter).toEntity(createMemberRequest, encodedPassword);
        verify(roleRepository).findByRoleType(RoleType.MEMBER);
        verify(memberDtoConverter).toCreateMemberResponse(member);
    }

}
