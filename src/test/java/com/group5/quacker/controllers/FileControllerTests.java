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
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.FileMapRepository;
import com.group5.quacker.services.AccountService;
import com.group5.quacker.services.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @MockBean
    private AccountService accountService;

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
        fileMap.setSize(1000000L);

        testdataText = new ByteArrayInputStream("this file is for automated testing".getBytes());
    }

    @Test
    @DisplayName("GET request to /files returns a file")
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
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/files/publicId"));

        verify(fileService, atLeast(0)).storeFile(argCaptor.capture());
        assertThat(argCaptor.getValue(), equalTo(mockMultipartFile));
    }

    @Test
    @DisplayName("Get request to /stream should return a partial response")
    public void shouldGetPartial(@TempDir Path tempDir) throws Exception {
        Path testFile = tempDir.resolve("testFile");
        List<String> lines = Arrays.asList("this file is for automated testing");
        Files.write(testFile, lines);

        when(fileMapRepository.findByPublicId("fileId")).thenReturn(this.fileMap);
        when(fileService.getFile("text.txt")).thenReturn(testFile.toFile());

        this.mockMvc
                .perform(get("/stream/fileId").header("Range", "bytes=0-"))
                .andExpect(content().string(containsString("this file is for automated testing")))
                .andExpect(header().string("Content-Type", "text/plain"))
                .andExpect(header().string("Content-Range", "bytes 0-34/35"))
                .andExpect(status().isPartialContent());
    }

    @Test
    @DisplayName("Get request to /stream without range header should return bad request")
    public void shouldReturnBadWithMissingHeader() throws Exception {
        this.mockMvc
                .perform(get("/stream/fileId"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get request to /gdpr returns a zip file")
    @WithMockUser(username = "test", password = "pwd", roles = "USER")
    public void testGdprEndpointShouldReturnZipFile() throws Exception {
        User user = new User();
        user.setName("test");
        user.setPasswordHash("hashyhash");
        user.setEmail("test@quacker.com");
        user.setQuacks(new ArrayList<>());
        when(accountService.currentUser()).thenReturn(user);
        this.mockMvc
                .perform(get("/gdpr"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/zip"));
    }
}
