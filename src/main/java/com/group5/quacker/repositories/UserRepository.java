package com.group5.quacker.repositories;

import com.group5.quacker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA repository for users
 */
public interface UserRepository  extends JpaRepository<User, Long> {
    User findByName(String username);
    List<User> findByNameContaining(String name);
    User findByEmailIs(String email);
}
