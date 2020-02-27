package com.group5.quacker.controllers;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.entities.Quack;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.QuackRepository;
import com.group5.quacker.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(UserController.class)
@TestPropertySource(properties = {"QUACKER_FILE_STORAGE = foo"})
@AutoConfigureMockMvc
class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private QuackRepository quackRepository;

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
        user.setFollowers(new ArrayList<>());

        quack = new Quack();
        quack.setAttachment(fileMap);
        quack.setDatePosted(new Date());
        quack.setFormattedDate(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(quack.getDatePosted()));
        quack.setPoster(user);
        quack.setQuackMessage("test message");
        quack.setId(123);
        quack.setLikes(1);
        quack.setLikers(new ArrayList<>());

        user.addQuack(quack);

        testStream = new ByteArrayInputStream("this file is for automated testing".getBytes());
    }

    @Test
    @DisplayName("Image for quack gets included")
    @WithMockUser(username = "test", password = "pwd", roles = "USER")
    void getUserPageWhenQuackHasImage() throws Exception {
        when(userRepository.findByName(anyString())).thenReturn(user);
        when(quackRepository.findByPoster(any(User.class))).thenReturn(user.getQuacks());

        MvcResult mvcResult = this.mockMvc
                .perform(get("/user/test"))
                .andExpect(model().attribute("userquacks", user.getQuacks()))
                .andReturn();

        List<Quack> userquacks = (List<Quack>) mvcResult.getModelAndView().getModel().get("userquacks");
        assertEquals(userquacks.get(0).getAttachment().getOriginalFileName(), "originalName.png");
    }
}