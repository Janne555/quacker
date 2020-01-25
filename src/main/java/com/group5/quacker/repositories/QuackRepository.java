package com.group5.quacker.repositories;

import com.group5.quacker.entities.Quack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuackRepository extends JpaRepository<Quack, Long> {
    Quack findById(long id);
}
