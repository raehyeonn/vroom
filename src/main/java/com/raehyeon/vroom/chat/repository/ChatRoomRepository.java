package com.raehyeon.vroom.chat.repository;

import com.raehyeon.vroom.chat.domain.ChatRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByCode(String roomCode);

}
