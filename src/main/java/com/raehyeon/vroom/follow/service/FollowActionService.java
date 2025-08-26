package com.raehyeon.vroom.follow.service;

import jakarta.persistence.OptimisticLockException;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowActionService {

    private final FollowService followService;

    public void executeFollowMember(UserDetails userDetails, String nickname) {
        int retries = 0;

        while (retries < 10) {
            try {
                followService.followMember(userDetails, nickname);

                return;
            } catch (OptimisticLockException | ObjectOptimisticLockingFailureException | CannotAcquireLockException e) {
                retries++;
                log.warn("락 충돌 발생 - {}회차 재시도", retries, e);

                int delay = ThreadLocalRandom.current().nextInt(10, 1000);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        throw new RuntimeException("팔로우 실패");
    }

    public void executeUnfollowMember(UserDetails userDetails, String nickname) {
        int retries = 0;

        while (retries < 10) {
            try {
                followService.followMember(userDetails, nickname);

                return;
            } catch (OptimisticLockException | ObjectOptimisticLockingFailureException | CannotAcquireLockException e) {
                retries++;
                log.warn("락 충돌 발생 - {}회차 재시도", retries, e);

                int delay = ThreadLocalRandom.current().nextInt(10, 1000);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        throw new RuntimeException("언팔로우 실패");
    }

    public void executeRemoveFollower(UserDetails userDetails, String nickname) {
        int retries = 0;

        while (retries < 10) {
            try {
                followService.followMember(userDetails, nickname);

                return;
            } catch (OptimisticLockException | ObjectOptimisticLockingFailureException | CannotAcquireLockException e) {
                retries++;
                log.warn("락 충돌 발생 - {}회차 재시도", retries, e);

                int delay = ThreadLocalRandom.current().nextInt(10, 1000);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        throw new RuntimeException("필로워 삭제 실패");
    }

}
