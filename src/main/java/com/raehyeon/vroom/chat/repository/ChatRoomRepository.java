package com.raehyeon.vroom.chat.repository;

import com.raehyeon.vroom.chat.domain.ChatRoom;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Page<ChatRoom> findByHiddenFalse(Pageable pageable);
    Optional<ChatRoom> findByCode(String roomCode);

}
