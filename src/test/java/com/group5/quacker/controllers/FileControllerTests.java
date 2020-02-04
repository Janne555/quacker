package com.group5.quacker.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.repositories.FileMapRepository;
import com.group5.quacker.services.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;


@WebMvcTest(FileController.class)
@TestPropertySource(properties = {"QUACKER_FILE_STORAGE = foo"})
@AutoConfigureMockMvc
public class FileControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private FileMapRepository fileMapRepository;

    private FileMap fileMap;
    private InputStream testdataText;

    @BeforeEach
    public void before() {
        fileMap = new FileMap();
        fileMap.setContentType("text/plain");
        fileMap.setFileName("text.txt");
        fileMap.setOriginalFileName("originalName.txt");
        testdataText = getClass().getResourceAsStream("/testdata/text.txt");
    }

    @Test
    @DisplayName("Test that a GET request returns a file")
    public void shouldReturnFileTest() throws Exception {
        when(fileMapRepository.findByPublicId("fileId")).thenReturn(this.fileMap);
        when(fileService.getInputStream("text.txt")).thenReturn(this.testdataText);

        this.mockMvc
                .perform(get("/files/fileId"))
                .andDo(print())
                .andExpect(content().string(containsString("this file is for automated testing")))
                .andExpect(header().string("Content-Type", "text/plain"));
    }

    @Test
    @DisplayName("Test that a GET request using 'with-original-name' adds a header with file name")
    public void shouldReturnFileTest2() throws Exception {
        when(fileMapRepository.findByPublicId("fileId")).thenReturn(fileMap);
        when(fileService.getInputStream("text.txt")).thenReturn(testdataText);

        mockMvc
                .perform(get("/files/fileId?with-original-name=true"))
                .andDo(print())
                .andExpect(content().string(containsString("this file is for automated testing")))
                .andExpect(header().string("Content-disposition", "attachment; filename=originalName.txt"));
    }
}
