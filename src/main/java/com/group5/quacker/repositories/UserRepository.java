package com.group5.quacker.repositories;

import com.group5.quacker.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByName(String name);
    List<User> allUsers();
}
