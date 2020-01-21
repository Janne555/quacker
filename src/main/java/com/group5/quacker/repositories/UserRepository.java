package com.group5.quacker.repositories;

import com.group5.quacker.entities.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRepository {
    private List<User> users = new ArrayList<>();

    public synchronized User saveUser(User user) {
        users.add(user);
        return user;
    }

    public synchronized boolean removeUser(User user) {
        users = users.stream().filter(u -> u.getId() != user.getId()).collect(Collectors.toList());
        return true;
    }

    public synchronized  User findByName(String name) {
        return users.stream()
                .filter(u -> u.getName() == name)
                .findFirst()
                .orElse(null);
    }
}
