package com.group5.quacker.controllers;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.entities.Quack;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.QuackRepository;
import com.group5.quacker.repositories.UserRepository;
import com.group5.quacker.services.AccountService;
import com.group5.quacker.services.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuackController.class)
@TestPropertySource(properties = {"QUACKER_FILE_STORAGE = foo"})
@AutoConfigureMockMvc
public class QuackControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private QuackRepository quackRepository;

    @MockBean
    private AccountService accountService;

    private User user;
    private FileMap fileMap;
    private InputStream testStream;
    private Quack quack;

    @BeforeEach
    public void beforeEach() {
        fileMap = new FileMap();
        fileMap.setContentType("image/plain");
        fileMap.setFileName("duckpick.png");
        fileMap.setOriginalFileName("originalName.png");
        fileMap.setPublicId("publicId");

        user = new User();
        user.setName("test");
        user.setPasswordHash("hashyhash");
        user.setProfilePhoto(fileMap);
        user.setEmail("test@quacker.com");
        user.setQuacks(new ArrayList<>());
        testStream = new ByteArrayInputStream("this file is for automated testing".getBytes());

        quack = new Quack();
        quack.setId(341234);
        quack.setPoster(user);
    }

    @Test
    @DisplayName("Posting a quack with file works")
    @WithMockUser(username = "test", password = "pwd", roles = "USER")
    public void postQuackWithFile() throws Exception {
        when(userRepository.findByName(anyString())).thenReturn(user);
        when(fileService.storeFile(any(MultipartFile.class))).thenReturn(fileMap);

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "originalName.png",
                "multipart/form-data",
                testStream
        );

        ArgumentCaptor<MultipartFile> argCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        ArgumentCaptor<Quack> quackCaptor = ArgumentCaptor.forClass(Quack.class);

        mockMvc
                .perform(multipart("/quack")
                        .file(mockMultipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                        .param("message", "test message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(fileService, atLeast(1)).storeFile(argCaptor.capture());
        assertThat(argCaptor.getValue(), equalTo(mockMultipartFile));
        verify(quackRepository).save(quackCaptor.capture());
        assertThat(quackCaptor.getValue().getAttachment().getOriginalFileName(), equalTo("originalName.png"));
    }

    @Test
    @DisplayName("Deleting a quack works")
    @WithMockUser(username = "test", password = "pwd", roles = "USER")
    public void deleteQuack() throws Exception {
        when(quackRepository.findById(anyLong())).thenReturn(quack);
        when(accountService.currentUser()).thenReturn(user);

        mockMvc
                .perform(delete("/quack/341234").with(csrf()))
                .andExpect(status().isNoContent());

        verify(quackRepository, atLeast(1)).delete(any(Quack.class));
    }


    @Test
    @DisplayName("Deleting someone else's quack doesn't delete it")
    @WithMockUser(username = "test", password = "pwd", roles = "USER")
    public void deleteOtherQuack() throws Exception {
        when(quackRepository.findById(anyLong())).thenReturn(quack);
        when(accountService.currentUser()).thenReturn(new User());

        mockMvc
                .perform(delete("/quack/341234").with(csrf()))
                .andExpect(status().isForbidden());

        verify(quackRepository, never()).delete(any(Quack.class));
    }
}
