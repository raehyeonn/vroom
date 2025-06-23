package com.raehyeon.vroom.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetChatRoomByCodeResponse {

    private long id;
    private String name;

}
