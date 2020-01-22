package com.group5.quacker.services;

import com.group5.quacker.dtos.UserDto;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerNewUserAccount(UserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setPasswordHash(passwordEncoder.encode((dto.getPassword())));
        user.setRoles(Arrays.asList("customer"));
        repository.save(user);
        return user;
    }
}
