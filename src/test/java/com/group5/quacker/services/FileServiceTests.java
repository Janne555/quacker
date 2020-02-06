package com.group5.quacker.services;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.repositories.FileMapRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = {"QUACKER_FILE_STORAGE = foo"})
public class FileServiceTests {
    @MockBean
    private FileMapRepository fileMapRepository;

    @MockBean
    private IdService idService;

    @MockBean
    private FileMakerService fileMakerService;

    @Autowired
    private FileService fileService;

    @TempDir
    File tempDir;

    @Test
    @DisplayName("store file works")
    public void storeFileTest() throws IOException {
        when(idService.generate()).thenReturn("first-id", "second-id");
        when(fileMakerService.makeFile(anyString())).thenReturn(new File(tempDir, "testFile"));

        InputStream testdataText = new ByteArrayInputStream("this file is for automated testing".getBytes());
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "uploadOriginalFileName.txt",
                "multipart/form-data",
                testdataText
        );

        FileMap fileMap = fileService.storeFile(mockMultipartFile);

        assertEquals("first-id", fileMap.getFileName());
        assertEquals("second-id", fileMap.getPublicId());
    }
}
