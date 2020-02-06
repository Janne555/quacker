package com.group5.quacker.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileMakerService {
    @Value("${fileStorage.path}")
    private String FILEPATH;

    public File makeFile(String fileName) {
        return new File(FILEPATH + "/" + fileName);
    }
}
