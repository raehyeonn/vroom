package com.raehyeon.vroom.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateChatRoomRequest {

    private String name;
    private boolean hidden;
    private boolean passwordRequired;
    private String password;

}
