package com.racha.ChatWithMe.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.racha.ChatWithMe.model.File;

@Repository
public interface FileRepository extends MongoRepository<File, String> {
    Optional<File> findTopByOrderByIdDesc();
}
