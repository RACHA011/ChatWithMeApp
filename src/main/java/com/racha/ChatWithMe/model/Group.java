package com.racha.ChatWithMe.model;

import java.util.List;

import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "group")
public class Group {
    @Id
    private String chatRoomId;

    @Size(min = 3, max = 20)
    private String groupName;

    private String description;

    private String icon;

    private String admin;

    private List<String> admins;

    private List<String> members;

    private String createdBy;

    private String timestamp;
}
