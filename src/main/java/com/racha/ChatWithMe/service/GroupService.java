package com.racha.ChatWithMe.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.racha.ChatWithMe.model.Group;
import com.racha.ChatWithMe.repository.GroupRepository;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    public Group save(Group group) {
        return groupRepository.save(group);
    }

    public Optional<Group> findGroupById(String chatRoomId) {
        return groupRepository.findById(chatRoomId);
    }

    public void deleteGroup(String chatRoomId) {
        groupRepository.deleteById(chatRoomId);
    }
}
