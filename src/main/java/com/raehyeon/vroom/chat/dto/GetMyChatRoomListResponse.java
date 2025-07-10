package com.raehyeon.vroom.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMyChatRoomListResponse {

    private long id;
    private String name;
    private boolean passwordRequired;

}
