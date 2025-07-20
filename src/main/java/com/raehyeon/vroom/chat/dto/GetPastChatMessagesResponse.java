package com.raehyeon.vroom.chat.dto;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetPastChatMessagesResponse {

    private List<ChatMessageDto> chatMessages;
    private ZonedDateTime nextCursor;
    private boolean hasNext;

}
