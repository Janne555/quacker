package com.group5.quacker.services;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.repositories.FileMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.UUID;

@Service
public class FileService {
    @Autowired
    FileMapRepository fileMapRepository;

    @Value("${fileStorage.path}")
    private String FILEPATH;

    public File getFile(String fileName) {
        return new File(FILEPATH + "/" + fileName);
    }

    public InputStream getInputStream(String fileName) throws FileNotFoundException {
        File file = getFile(fileName);
        if (file == null) {
            return null;
        }
        return new FileInputStream(file);
    }

    public FileMap storeFile(MultipartFile file) throws IOException {
        FileMap fileMap = new FileMap();
        fileMap.setFileName(UUID.randomUUID().toString());
        fileMap.setPublicId(UUID.randomUUID().toString());
        fileMap.setContentType(file.getContentType());
        fileMap.setOriginalFileName(file.getOriginalFilename());
        fileMapRepository.save(fileMap);

        file.transferTo(new File(FILEPATH + "/" + fileMap.getFileName()));
        return fileMap;
    }
}
