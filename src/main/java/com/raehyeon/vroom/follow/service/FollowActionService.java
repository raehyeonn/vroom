package com.raehyeon.vroom.follow.service;

import com.raehyeon.vroom.follow.dto.FollowRequest;
import com.raehyeon.vroom.follow.dto.UnfollowRequest;
import jakarta.persistence.OptimisticLockException;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowActionService {

    private final FollowService followService;

    public void executeFollow(UserDetails userDetails, FollowRequest followRequest) {
        int retries = 0;

        while (retries < 3) {
            try {
                followService.follow(userDetails, followRequest);
                return;
            } catch (OptimisticLockException e) {
                retries++;

                int backoff = (int) Math.pow(2, retries) * 75;
                int delay = ThreadLocalRandom.current().nextInt(backoff, backoff + 200);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {}
            }
        }

        throw new RuntimeException("팔로우 실패");
    }

    public void executeUnfollow(UserDetails userDetails, UnfollowRequest unfollowRequest) {
        int retries = 0;

        while (retries < 3) {
            try {
                followService.unfollow(userDetails, unfollowRequest);
                return;
            } catch (OptimisticLockException e) {
                retries++;

                int backoff = (int) Math.pow(2, retries) * 75;
                int delay = ThreadLocalRandom.current().nextInt(backoff, backoff + 200);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {}
            }
        }

        throw new RuntimeException("언팔로우 실패");
    }

}
