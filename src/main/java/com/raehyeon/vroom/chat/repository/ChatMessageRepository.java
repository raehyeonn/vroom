package com.raehyeon.vroom.chat.repository;

import com.raehyeon.vroom.chat.domain.ChatMessage;
import com.raehyeon.vroom.chat.domain.ChatRoom;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m JOIN FETCH m.sender WHERE m.chatRoom = :chatRoom ORDER BY m.sentAt DESC")
    List<ChatMessage> findTop21ByChatRoomOrderBySentAtDesc(@Param("chatRoom") ChatRoom chatRoom, Pageable pageable);

    @Query("SELECT m FROM ChatMessage m JOIN FETCH m.sender WHERE m.chatRoom = :chatRoom AND m.sentAt < :cursor ORDER BY m.sentAt DESC")
    List<ChatMessage> findTop21ByChatRoomAndSentAtBeforeOrderBySentAtDesc(@Param("chatRoom") ChatRoom chatRoom, @Param("cursor") ZonedDateTime cursor, Pageable pageable);

}
