package com.group5.quacker.services;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.repositories.FileMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.net.MalformedURLException;
import java.util.UUID;

@Service
public class FileService {
    @Autowired
    private FileMapRepository fileMapRepository;

    @Autowired
    private IdService idService;

    @Autowired
    private FileMakerService fileMakerService;

    public File getFile(String fileName) {
        return fileMakerService.makeFile(fileName);
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
        fileMap.setFileName(idService.generate());
        fileMap.setPublicId(idService.generate());
        fileMap.setContentType(file.getContentType());
        fileMap.setOriginalFileName(file.getOriginalFilename());
        fileMap.setSize(file.getSize());
        fileMapRepository.save(fileMap);

        file.transferTo(fileMakerService.makeFile(fileMap.getFileName()));
        return fileMap;
    }

    public UrlResource getAsUrlResource(FileMap fileMap) throws MalformedURLException {
        return fileMakerService.getFileAsUrlResource(fileMap);
    }
}
