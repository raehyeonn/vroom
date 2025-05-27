package com.raehyeon.vroom.chat.dto;

import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateChatRoomResponse {

    private long id;
    private String name;
    private String code;
    private ZonedDateTime createdAt;

}
