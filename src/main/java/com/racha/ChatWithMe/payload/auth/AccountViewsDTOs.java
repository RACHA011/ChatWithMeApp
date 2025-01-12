package com.racha.ChatWithMe.payload.auth;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountViewsDTOs {
    private String id;

    private String username;

    private String ppicture;

    private List<String> friends = new ArrayList<>();

    private List<String> friendRequests = new ArrayList<>();

    private List<String> sentFriendRequests = new ArrayList<>();
}
