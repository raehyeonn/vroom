package com.raehyeon.vroom.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetAllChatRoomsResponse {

    private long id;
    private String name;

}
