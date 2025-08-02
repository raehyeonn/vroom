package com.raehyeon.vroom.follow.repository;

import com.raehyeon.vroom.follow.domain.Follow;
import com.raehyeon.vroom.member.domain.Member;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(Member follower, Member following);
    Optional<Follow> findByFollowerAndFollowing(Member follower, Member following);
    Page<Follow> findAllByFollower(Member follower, Pageable pageable);
    Page<Follow> findAllByFollowing(Member following, Pageable pageable);

}
