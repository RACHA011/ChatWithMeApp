package com.racha.ChatWithMe.payload.chat.group;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {
    @Size(min = 3, max = 20)
    @Schema(example = "groupname", description = "This is the group name", required = true)
    private String groupName;

    @Schema(example = "groupdescription", description = "This is the group description")
    private String description;

    @Schema(example = "groupicon", description = "This is the group icon")
    private String icon;

    @Schema(example = "[\"user1@example.com\", \"user2@example.com\", \"user3@example.com\"]", description = "These are the group members", required = true)
    private List<String> members;

    @Schema(example = "2022-07-15T14:30:59Z", description = "This is the group creation timestamp", required = true)
    private String timestamp;
}
