package com.raehyeon.vroom.member.domain;

import com.raehyeon.vroom.chat.domain.ChatRoomParticipant;
import com.raehyeon.vroom.role.domain.MemberRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

@Entity
@Table(name = "members")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<MemberRole> memberRoles = new ArrayList<>();

    @Column(nullable = false)
    @CreationTimestamp(source = SourceType.DB)
    private ZonedDateTime createdAt;

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ChatRoomParticipant> chatRoomParticipants = new ArrayList<>();

    // 나를 팔로우 하는 사람들
    @OneToMany(mappedBy = "following")
    private List<Follow> followers = new ArrayList<>();

    // 내가 팔로우 하는 사람들
    @OneToMany(mappedBy = "follower")
    private List<Follow> followings = new ArrayList<>();

}
