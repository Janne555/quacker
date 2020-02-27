package com.group5.quacker.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service used to generate a random UUID string
 */
@Service
public class IdService {
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
