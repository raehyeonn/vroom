package com.raehyeon.vroom.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetChatRoomParticipantResponse {

    private Long id;
    private String nickname;

}
