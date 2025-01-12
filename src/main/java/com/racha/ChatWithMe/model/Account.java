package com.racha.ChatWithMe.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.racha.ChatWithMe.utils.constants.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "account")
public class Account {
    @Id
    private String id;

    @Indexed(unique = true)
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Indexed(unique = true)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    private String ppicture;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    private List<String> friends = new ArrayList<>();

    private List<String> friendRequests = new ArrayList<>();

    private List<String> sentFriendRequests = new ArrayList<>();

    private Status status;

    private String authorities;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Account account = (Account) o;
        return id != null && id.equals(account.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
