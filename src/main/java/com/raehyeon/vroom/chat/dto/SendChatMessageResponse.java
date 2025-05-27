package com.raehyeon.vroom.chat.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendChatMessageResponse {

    private Long chatRoomId;
    private Long senderId;
    private String senderNickname;
    private String content;
    private ZonedDateTime sentAt;

}
