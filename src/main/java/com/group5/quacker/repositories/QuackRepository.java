package com.group5.quacker.repositories;

import com.group5.quacker.entities.Quack;
import com.group5.quacker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA repository for quacks
 */
public interface QuackRepository extends JpaRepository<Quack, Long> {
    Quack findById(long id);
    List<Quack> findByPoster(User user);
    List<Quack> findAllByLikersContains(User user);
    List<Quack> findByQuackMessageContaining(String quackMessage);
    List<Quack> findByPublicClassificationTrue();
}
