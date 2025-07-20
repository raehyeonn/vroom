package com.raehyeon.vroom.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangeRoomNameRequest {

    private String roomName;

}
