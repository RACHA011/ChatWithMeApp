package com.racha.ChatWithMe.service;

import java.io.File;
import java.io.FileInputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Service
public class FirebaseService {

    // @Value("${firebase.service-account-file-path}")
    private String serviceAccountFilePath = "src/main/resources/static/key/chatwithme-033-firebase-adminsdk-stxjp-4933c87581.json";

    @Value("${firebase.database.url}")
    private String databaseUrl;

    @PostConstruct
    public void initializeFirebase() throws Exception {
        // Load the service account key from the specified path
        File file = ResourceUtils.getFile(serviceAccountFilePath);
        try (FileInputStream serviceAccount = new FileInputStream(file)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(databaseUrl)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase successfully initialized");
            } else {
                System.out.println("Firebase app already initialized");
            }
        }
    }
}
