package com.group5.quacker.repositories;

import com.group5.quacker.entities.Quack;
import com.group5.quacker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA repository for quacks
 */
public interface QuackRepository extends JpaRepository<Quack, Long> {
    /**
     * @param id ID of quack to be searched
     * @return Quack object if one was found
     */
    Quack findById(long id);

    /**
     * @param user Username whos quacks are searched
     * @return List of quack objects if quacks were found
     */
    List<Quack> findByPoster(User user);

    /**
     * Find all quacks that a user has liked
     * @param user User that has liked quacks
     * @return List of quacks that the user has liked
     */
    List<Quack> findAllByLikersContains(User user);

    /**
     * Find all quacks that contain the provided string
     * @param quackMessage Message string for search
     * @return List of quacks that contain the provided string
     */
    List<Quack> findByQuackMessageContaining(String quackMessage);
}
