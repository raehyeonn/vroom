package com.raehyeon.vroom.member;

import static org.assertj.core.api.Assertions.assertThat;

import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
            .email("test@example.com")
            .password("password1!")
            .nickname("tester")
            .build();

        memberRepository.save(member);
    }

    @Test
    @DisplayName("이메일로 회원 존재 여부 확인")
    void existsByEmail_shouldReturnTrueIfExists() {
        boolean exists = memberRepository.existsByEmail("test@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("닉네임으로 회원 존재 여부 확인")
    void existsByNickname_shouldReturnTrueIfExists() {
        boolean exists = memberRepository.existsByNickname("tester");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일로 회원 조회")
    void findByEmail_shouldReturnMember() {
        Optional<Member> result = memberRepository.findByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getNickname()).isEqualTo("tester");
    }

}
