package com.group5.quacker.controllers;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.UserRepository;
import com.group5.quacker.services.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SettingsController.class)
@TestPropertySource(properties = {"QUACKER_FILE_STORAGE = foo"})
@AutoConfigureMockMvc
public class SettingsControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private UserRepository userRepository;

    private User user;
    private FileMap fileMap;
    private InputStream testStream;

    @BeforeEach
    public void beforeEach() {
        fileMap = new FileMap();
        fileMap.setContentType("text/plain");
        fileMap.setFileName("duckpick.png");
        fileMap.setOriginalFileName("originalName.txt");
        fileMap.setPublicId("publicId");

        user = new User();
        user.setName("test");
        user.setPasswordHash("hashyhash");
        user.setProfilePhoto(fileMap);
        user.setEmail("test@quacker.com");

        testStream = new ByteArrayInputStream("this file is for automated testing".getBytes());
    }

    @Test
    @DisplayName("Unauthorized request redirects to login")
    public void noAuthRedirectTest() throws Exception {
        when(userRepository.findByName(anyString())).thenReturn(null);

        this.mockMvc
                .perform(get("/settings/anything_here"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("Profile photo gets added when setting is profile-photo")
    @WithMockUser(username = "test", password = "pwd", roles = "USER")
    public void profilePhotoIncludedTest() throws Exception {
        when(userRepository.findByName(anyString())).thenReturn(user);

        this.mockMvc
                .perform(get("/settings/profile-photo"))
                .andExpect(model().attribute("profilePhotoUrl", "/files/publicId"));
    }

    @Test
    @DisplayName("Post to /settings/profile-photo uploads file and redirects back to same page")
    @WithMockUser(username = "test", password = "pwd", roles = "USER")
    public void postProfilePhotoTest() throws Exception {
        when(userRepository.findByName(anyString())).thenReturn(user);
        when(fileService.storeFile(any(MultipartFile.class))).thenReturn(fileMap);

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "originalName.png",
                "multipart/form-data",
                testStream
        );

        ArgumentCaptor<MultipartFile> argCaptor = ArgumentCaptor.forClass(MultipartFile.class);

        mockMvc
                .perform(multipart("/settings/profile-photo").file(mockMultipartFile).contentType(MediaType.MULTIPART_FORM_DATA).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile-photo"));

        verify(fileService, atLeast(0)).storeFile(argCaptor.capture());
        assertThat(argCaptor.getValue(), equalTo(mockMultipartFile));
    }
}
