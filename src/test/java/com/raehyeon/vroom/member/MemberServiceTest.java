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

    @Mock private PasswordEncoder passwordEncoder;
    @Mock private MemberRepository memberRepository;
    @Mock private MemberRoleRepository memberRoleRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private MemberEntityConverter memberEntityConverter;
    @Mock private MemberDtoConverter memberDtoConverter;

    @InjectMocks private MemberService memberService;

    private CreateMemberRequest createMemberRequest;
    private Member member;
    private Role role;
    private MemberRole memberRole;

    @BeforeEach
    void setUp() {
        createMemberRequest = CreateMemberRequest.builder()
            .email("test@example.com")
            .password("password1!")
            .nickname("tester")
            .build();

        member = Member.builder()
            .id(1L)
            .email("test@example.com")
            .password("password1!")
            .nickname("tester")
            .memberRoles(new ArrayList<>())
            .createdAt(ZonedDateTime.now())
            .build();

        role = Role.builder()
            .roleType(RoleType.MEMBER)
            .build();

        memberRole = MemberRole.create(member, role);
        member.getMemberRoles().add(memberRole);

    }

    @Test
    @DisplayName("회원 가입 - 이메일 중복 예외")
    void 회원가입_시_이메일_중복_예외_발생() {
        // 이메일이 이미 존재하는 경우를 가정
        when(memberRepository.existsByEmail(createMemberRequest.getEmail())).thenReturn(true);

        // MemberService 클래스의 createMember 메서드 호출 시
        // 던져진 예외가 DuplicateEmailException 클래스의 인스턴스인지 확인하고,
        // "이미 사용 중인 이메일입니다."라는 메시지를 포함하고 있는지 확인
        assertThatThrownBy(() -> memberService.createMember(createMemberRequest))
            .isInstanceOf(DuplicateEmailException.class)
            .hasMessage("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("회원 가입 - 닉네임 중복 예외")
    void 회원가입_시_닉네임_중복_예외_발생() {
        // 닉네임이 이미 존재하는 경우를 가정
        when(memberRepository.existsByNickname(createMemberRequest.getNickname())).thenReturn(true);

        // MemberService 클래스의 createMember 메서드 호출 시
        // 던져진 예외가 DuplicateNicknameException 클래스의 인스턴스인지 확인하고,
        // "이미 사용 중인 닉네임입니다."라는 메시지를 포함하고 있는지 확인
        assertThatThrownBy(() -> memberService.createMember(createMemberRequest))
            .isInstanceOf(DuplicateNicknameException.class)
            .hasMessage("이미 사용 중인 닉네임입니다.");
    }

    @Test
    @DisplayName("회원 가입 - 성공")
    void 회원가입_성공() {
        String encodedPassword = "encodedPassword";

        // 이메일 중복 검사 통과
        when(memberRepository.existsByEmail(createMemberRequest.getEmail())).thenReturn(false);

        // 닉네임 중복 검사 통과
        when(memberRepository.existsByNickname(createMemberRequest.getNickname())).thenReturn(false);

        // 암호화 통과
        when(passwordEncoder.encode(createMemberRequest.getPassword())).thenReturn(encodedPassword);

        // MemberEntityConverter가 회원가입 요청 DTO와 인코딩된 비밀번호로 Member 엔티티를 만드는 과정
        when(memberEntityConverter.toEntity(createMemberRequest, encodedPassword)).thenReturn(member);


        when(roleRepository.findByRoleType(RoleType.MEMBER)).thenReturn(Optional.of(role));
        when(memberDtoConverter.toCreateMemberResponse(member)).thenReturn(
            CreateMemberResponse.builder()
                .id(1L)
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build()
        );

        // when
        CreateMemberResponse response = memberService.createMember(createMemberRequest);

        // then
        assertEquals(member.getEmail(), response.getEmail());
        assertEquals(member.getNickname(), response.getNickname());

        verify(memberRepository).save(member);
        verify(memberRoleRepository).save(any(MemberRole.class));
        verify(passwordEncoder).encode(createMemberRequest.getPassword());
        verify(memberEntityConverter).toEntity(createMemberRequest, encodedPassword);
        verify(roleRepository).findByRoleType(RoleType.MEMBER);
        verify(memberDtoConverter).toCreateMemberResponse(member);
    }



}
