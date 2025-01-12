package com.racha.ChatWithMe.entity;

import com.racha.ChatWithMe.utils.constants.MessageType;

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
public class Content {
    private MessageType type; 
    private String data; 

}