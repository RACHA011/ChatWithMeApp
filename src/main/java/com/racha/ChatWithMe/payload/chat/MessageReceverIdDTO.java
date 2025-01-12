package com.racha.ChatWithMe.payload.chat;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class MessageReceverIdDTO {
    @NotNull
    @Schema(example = "2", description = "Unique identifier for the receiver")
    private String receiverId;
}
