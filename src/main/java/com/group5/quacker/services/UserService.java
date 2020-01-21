package com.group5.quacker.services;

import com.group5.quacker.dtos.UserDto;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    @Transactional
    public User registerNewUserAccount(UserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        repository.saveUser(user);
        return user;
    }
}
