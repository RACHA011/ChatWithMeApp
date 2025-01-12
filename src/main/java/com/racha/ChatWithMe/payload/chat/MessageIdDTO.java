package com.racha.ChatWithMe.payload.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageIdDTO {
    @Schema(example = "1", description = "A unique ID for the message",  requiredMode = RequiredMode.REQUIRED)
    private String id;

    @Schema(example = "group_chat_12345", description = "A unique ID for the chat room (only for group messages)", requiredMode = RequiredMode.REQUIRED)
    private String chatRoomId;
}
