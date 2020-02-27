package com.group5.quacker.services;

import com.group5.quacker.entities.FileMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Service used to decouple file creation in the file storage process
 *
 */
@Service
public class FileMakerService {
    @Value("${fileStorage.path}")
    private String FILEPATH;

    public File makeFile(String fileName) {
        return new File(FILEPATH + "/" + fileName);
    }

    public UrlResource getFileAsUrlResource(FileMap fileMap) throws MalformedURLException {
        return new UrlResource(String.format("file:%s/%s", FILEPATH, fileMap.getFileName()));
    }
}
