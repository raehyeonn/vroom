package com.raehyeon.vroom.security.jwt;

import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.repository.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다."));

        String email = member.getEmail();
        String encodedPassword = member.getPassword();
        List<GrantedAuthority> authorities = member.getMemberRoles().stream()
            .map(memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole.getRole().getRoleType().name()))
            .collect(Collectors.toList());

        return new User(email, encodedPassword, authorities);
    }

}
