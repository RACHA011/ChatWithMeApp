package com.racha.ChatWithMe.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.racha.ChatWithMe.model.Group;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {
    
}