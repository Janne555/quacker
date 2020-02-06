package com.group5.quacker.repositories;

import com.group5.quacker.entities.FileMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMapRepository extends JpaRepository<FileMap, Long> {
    FileMap findByPublicId(String uuid);
}
