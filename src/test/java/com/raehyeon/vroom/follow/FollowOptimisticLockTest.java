/*
package com.raehyeon.vroom.follow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.raehyeon.vroom.follow.service.FollowActionService;
import com.raehyeon.vroom.member.domain.Member;
import com.raehyeon.vroom.member.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.core.userdetails.User;

@SpringBootTest
@ActiveProfiles("dev")
public class FollowOptimisticLockTest {

    @Autowired private FollowActionService followActionService;
    @Autowired private MemberRepository memberRepository;

    private Member target;
    private List<Member> followers;

    @BeforeEach
    void setUp() {
        target = Member.builder()
            .email("testTarget20@example.com")
            .nickname("테스트타겟20")
            .password("password1234!")
            .build();
        memberRepository.save(target);

        followers = new ArrayList<>();
        for (int i = 8200; i < 8300; i++) {
            Member follower = Member.builder()
                .email("testFollower" + i + "@example.com")
                .nickname("테스트팔로워" + i)
                .password("password1234!")
                .build();
            memberRepository.save(follower);
            followers.add(follower);
        }

        memberRepository.flush();
    }

    @Test
    @DisplayName("100명의 사용자가 동시에 팔로우 요청 시 낙관적 락 및 성능 테스트")
    void testOptimisticLockingAndConcurrentFollowRequests() throws InterruptedException {
        int totalRequests = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(100); // 최대 100개의 스레드 풀
        CountDownLatch startLatch = new CountDownLatch(1); // 시작 신호
        CountDownLatch endLatch = new CountDownLatch(totalRequests); // 완료 대기
        AtomicInteger successCount = new AtomicInteger(0); // 성공 카운트
        AtomicInteger failureCount = new AtomicInteger(0); // 실패 카운트

        System.out.println("===== 100명이 동시에 한 명을 팔로우하는 테스트 시작 =====");

        // 100개의 작업을 스레드풀에 제출
        for (int i = 0; i < totalRequests; i++) {
            final int index = i;

            executorService.submit(() -> {
                try {
                    startLatch.await(); // 모든 스레드가 준비될 때까지 대기

                    Member follower = followers.get(index);
                    UserDetails userDetails = User
                        .withUsername(follower.getEmail())
                        .password("encodedPassword")
                        .authorities("ROLE_USER")
                        .build();

                    followActionService.executeFollowMember(userDetails, target.getNickname());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.out.println("팔로우 실패 [" + index + "]: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        Thread.sleep(1000);
        System.out.println("100개 스레드 동시 실행 시작");

        long startTime = System.currentTimeMillis();
        startLatch.countDown(); // 모든 스레드 동시 시작
        endLatch.await(); // 100개의 endLatch.countDown() 호출이 끝나면 await 해제
        executorService.shutdown(); // 모든 작업 종료 후 스레드 풀 종료

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // 결과 확인
        Member refreshedTarget = memberRepository.findById(target.getId()).orElseThrow(() -> new RuntimeException("타겟 멤버를 찾을 수 없습니다."));

        // 결과 출력
        System.out.println("\n===== 100개 스레드 동시 실행 결과 =====");
        System.out.println("총 소요 시간: " + totalTime + "ms");
        System.out.println("평균 처리 시간: " + (totalTime / (double) totalRequests) + "ms/request");
        System.out.println("성공한 팔로우 수: " + successCount.get());
        System.out.println("실패한 팔로우 수: " + failureCount.get());
        System.out.println("타겟 멤버의 최종 팔로워 수: " + refreshedTarget.getFollowerCount() + "\n");

        // 검증
        assertEquals(100, successCount.get(), "모든 팔로우 요청이 성공해야 합니다.");
        assertEquals(0, failureCount.get(), "실패한 팔로우 요청이 없어야 합니다.");
        assertEquals(100, refreshedTarget.getFollowerCount(), "타겟 멤버의 팔로워 수가 100명이어야 합니다.");
    }

}
*/
