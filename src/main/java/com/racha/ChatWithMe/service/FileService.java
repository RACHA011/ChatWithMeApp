package com.racha.ChatWithMe.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.racha.ChatWithMe.model.File;
import com.racha.ChatWithMe.repository.FileRepository;

@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;

    public File save(File file) {
        if (file.getId() == null || file.getId().isEmpty()) {
            Optional<File> maxIdOpt = findMaxId();
            String newidString = maxIdOpt.map(File::getId).orElse("0");
            Long newId = Long.parseLong(newidString) + 1;
            file.setId(newId + "");
        }
        return fileRepository.save(file);
    }
    
    public Optional<File> findById(String id) {
        return fileRepository.findById(id);
    }

    public Optional<File> findMaxId() {
        return fileRepository.findTopByOrderByIdDesc();
    }
}
