package com.raehyeon.vroom.follow.service;

import jakarta.persistence.OptimisticLockException;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowActionService {

    private final FollowService followService;

    public void executeFollowMember(UserDetails userDetails, String nickname) {
        int retries = 0;

        while (retries < 3) {
            try {
                followService.followMember(userDetails, nickname);

                return;
            } catch (OptimisticLockException e) {
                retries++;

                int backoff = (int) Math.pow(2, retries) * 75;
                int delay = ThreadLocalRandom.current().nextInt(backoff, backoff + 200);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {

                }
            }
        }

        throw new RuntimeException("팔로우 실패");
    }

    public void executeUnfollowMember(UserDetails userDetails, String nickname) {
        int retries = 0;

        while (retries < 3) {
            try {
                followService.unfollowMember(userDetails, nickname);

                return;
            } catch (OptimisticLockException e) {
                retries++;

                int backoff = (int) Math.pow(2, retries) * 75;
                int delay = ThreadLocalRandom.current().nextInt(backoff, backoff + 200);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {

                }
            }
        }

        throw new RuntimeException("언팔로우 실패");
    }

    public void executeRemoveFollower(UserDetails userDetails, String nickname) {
        int retries = 0;

        while (retries < 3) {
            try {
                followService.removeFollower(userDetails, nickname);

                return;
            } catch (OptimisticLockException e) {
                retries++;

                int backoff = (int) Math.pow(2, retries) * 75;
                int delay = ThreadLocalRandom.current().nextInt(backoff, backoff + 200);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {

                }
            }
        }

        throw new RuntimeException("팔로워 삭제 실패");
    }

}
