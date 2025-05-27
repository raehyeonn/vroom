package com.raehyeon.vroom.role.domain;

import com.raehyeon.vroom.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_roles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private MemberRole(Member member, Role role) {
        this.member = member;
        this.role = role;
    }

    public static MemberRole create(Member member, Role role) {
        return new MemberRole(member, role);
    }

}
