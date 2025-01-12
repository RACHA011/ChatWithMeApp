package com.racha.ChatWithMe.payload.chat;

import javax.validation.constraints.NotNull;

import com.racha.ChatWithMe.entity.Content;
import com.racha.ChatWithMe.utils.constants.ChatType;

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
public class MessageDTO {

    @NotNull
    @Schema(example = "2", description = "Unique identifier for the receiver")
    private String receiverId;

    @NotNull
    @Schema(description = "Message content containing type and data", requiredMode = RequiredMode.REQUIRED)
    private Content content;

    @NotNull
    @Schema(example = "2024-12-27T12:48:36Z", description = "The date and time in ISO 8601 format", requiredMode = RequiredMode.REQUIRED)
    private String timestamp;

    @NotNull
    @Schema(example = "PERSON_TO_PERSON", description = "Type of chat (PERSON_TO_PERSON or GROUP)", allowableValues = {
            "PERSON_TO_PERSON", "GROUP" }, requiredMode = RequiredMode.REQUIRED)
    private ChatType chatType;

    @Schema(example = "group_chat_12345", description = "A unique ID for the chat room (only for group messages)")
    private String chatRoomId; // Optional for PERSON_TO_PERSON, required for GROUP
}
