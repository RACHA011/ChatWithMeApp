package com.racha.ChatWithMe.model;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;

import com.racha.ChatWithMe.entity.Content;
import com.racha.ChatWithMe.utils.constants.ChatType;
import com.racha.ChatWithMe.utils.constants.MessageStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
// @Document(collection = "message")
public class Message {

    @Id
    private String id; // Unique identifier for the message

    @NotNull
    private String senderId;

    @NotNull
    private String receiverId; // For person-to-person messages, it's the other user's ID

    private Content content;

    private String timestamp; // Epoch time in milliseconds

    private MessageStatus status;

    private boolean read = false;

    @NotNull
    private ChatType chatType; // PERSON_TO_PERSON or GROUP

    private String chatRoomId; // Used only for group messages
}
