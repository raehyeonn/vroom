package com.raehyeon.vroom.chat.repository;

import com.raehyeon.vroom.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}
