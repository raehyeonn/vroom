package com.raehyeon.vroom.follow.repository;

import com.raehyeon.vroom.follow.domain.Follow;
import com.raehyeon.vroom.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(Member follower, Member following);
    Optional<Follow> findByFollowerAndFollowing(Member follower, Member following);

    @EntityGraph(attributePaths = {"following"})
    Page<Follow> findAllByFollower(Member follower, Pageable pageable);

    @EntityGraph(attributePaths = {"follower"})
    Page<Follow> findAllByFollowing(Member following, Pageable pageable);

    @EntityGraph(attributePaths = {"following"})
    List<Follow> findAllByFollowerAndFollowingIn(Member follower, List<Member> followingList);

}
