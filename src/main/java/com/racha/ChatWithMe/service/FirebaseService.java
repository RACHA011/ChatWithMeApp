package com.racha.ChatWithMe.service;

import java.io.FileInputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Service
public class FirebaseService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseService.class);

    @Value("${firebase.service-account-file-path}")
    private String serviceAccountFilePath;

    @Value("${firebase.database.url}")
    private String databaseUrl;

    @PostConstruct
    public void initializeFirebase() {
        try (FileInputStream serviceAccount = new FileInputStream(serviceAccountFilePath)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(databaseUrl)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase successfully initialized");
            } else {
                logger.info("Firebase app already initialized");
            }
        } catch (IOException e) {
            logger.error("Failed to initialize Firebase: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}