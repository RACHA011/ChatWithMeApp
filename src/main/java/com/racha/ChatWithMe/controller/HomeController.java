// package com.racha.ChatWithMe.controller;

// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.GetMapping;

// import io.swagger.v3.oas.annotations.tags.Tag;

// // add log.info("User {} logged in at {}", authentication.getName(), LocalDateTime.now()); i a later stage
// // also add change-password , reset-password

// @Controller
// @CrossOrigin(origins = {"http://localhost:3000", "*"}, maxAge = 3600, allowedHeaders = "*")
// @Tag(name = "Home Controller", description = "Controller for Home management")
// public class HomeController {
//     @GetMapping("/")
//     public String home() {
//         return "index";
//     }

//     @GetMapping("/login")
//     public String login() {
//         return "account_view/login";
//     }

//     @GetMapping("/logout")
//     public String logout() {
//         return "account_view/logout";
//     }

//     @GetMapping("/home")
//     public String register() {
//         return "chat_view/home";
//     }
// }
