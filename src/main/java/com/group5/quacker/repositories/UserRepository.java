package com.group5.quacker.repositories;

import com.group5.quacker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, Long> {
    User findByName(String username);
}
