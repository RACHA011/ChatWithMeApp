package com.racha.ChatWithMe.model;

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
@Document(collection = "file")
public class File {
    @Id
    private String id;

    private String fileName;

    private String contentType;

    private byte[] fileBytes;
}
