package com.racha.ChatWithMe.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "ChatWhithMe Api",
        version = "Versions 1.0",
        contact = @Contact(
            name = "rachadev", email = "rachadev032@gmail.com", url="/"
        ),
        description = "this is an api for the ChatWhithMe application"
    )
)
public class SwaggerConfig {
    
}