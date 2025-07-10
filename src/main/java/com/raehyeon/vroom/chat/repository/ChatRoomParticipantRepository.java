package com.raehyeon.vroom.chat.repository;

import com.raehyeon.vroom.chat.domain.ChatRoom;
import com.raehyeon.vroom.chat.domain.ChatRoomParticipant;
import com.raehyeon.vroom.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    boolean existsByMemberAndChatRoom(Member member, ChatRoom chatRoom);
    Page<ChatRoomParticipant> findAllByMember(Member member, Pageable pageable);

}
