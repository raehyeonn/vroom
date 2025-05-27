package com.raehyeon.vroom.chat.repository;

import com.raehyeon.vroom.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
