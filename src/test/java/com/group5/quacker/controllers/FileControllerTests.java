package com.group5.quacker.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.repositories.FileMapRepository;
import com.group5.quacker.services.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
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
    private MultipartFile multipartFile;

    @BeforeEach
    public void before() {
        fileMap = new FileMap();
        fileMap.setContentType("text/plain");
        fileMap.setFileName("text.txt");
        fileMap.setOriginalFileName("originalName.txt");
        fileMap.setPublicId("publicId");

        testdataText = new ByteArrayInputStream("this file is for automated testing".getBytes());;
    }

    @Test
    @DisplayName("GET request returns a file")
    public void shouldReturnFileTest() throws Exception {
        when(fileMapRepository.findByPublicId("fileId")).thenReturn(this.fileMap);
        when(fileService.getInputStream("text.txt")).thenReturn(this.testdataText);

        this.mockMvc
                .perform(get("/files/fileId"))
                .andExpect(content().string(containsString("this file is for automated testing")))
                .andExpect(header().string("Content-Type", "text/plain"));
    }

    @Test
    @DisplayName("GET request using 'with-original-name' adds a header with file name")
    public void shouldReturnFileWithNameTest() throws Exception {
        when(fileMapRepository.findByPublicId("fileId")).thenReturn(fileMap);
        when(fileService.getInputStream("text.txt")).thenReturn(testdataText);

        mockMvc
                .perform(get("/files/fileId?with-original-name=true"))
                .andExpect(content().string(containsString("this file is for automated testing")))
                .andExpect(header().string("Content-disposition", "attachment; filename=originalName.txt"));
    }

    @Test
    @DisplayName("POST request results in a successful upload")
    public void shouldUploadFileTest() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "uploadOriginalFileName.txt",
                "multipart/form-data",
                testdataText
        );
        when(fileService.storeFile(any(MultipartFile.class))).thenReturn(fileMap);
        ArgumentCaptor<MultipartFile> argCaptor = ArgumentCaptor.forClass(MultipartFile.class);

        mockMvc
                .perform(multipart("/files").file(mockMultipartFile).contentType(MediaType.MULTIPART_FORM_DATA).with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/files/publicId"));

        verify(fileService, atLeast(0)).storeFile(argCaptor.capture());
        assertThat(argCaptor.getValue(), equalTo(mockMultipartFile));
    }
}
