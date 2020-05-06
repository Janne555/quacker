package com.group5.quacker.repositories;


import com.group5.quacker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA repository for users
 */
public interface UserRepository  extends JpaRepository<User, Long> {
    /**
     * @param username Username to be searched from the JPA database
     * @return User object if one was found
     */
    User findByName(String username);

    /**
     * @param name Username or part of a username to be searched
     * @return List of users if the search criteria was met
     */
    List<User> findByNameContaining(String name);

    /**
     * @param email Email of user to be searched
     * @return User object if one was found with the provided email
     */
    User findByEmailIs(String email);
}
