package com.group5.quacker.controllers;

import com.group5.quacker.entities.Feedback;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.FeedbackRepository;
import com.group5.quacker.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelpController.class)
@TestPropertySource(properties = {"QUACKER_FILE_STORAGE = foo"})
@AutoConfigureMockMvc
public class HelpControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private FeedbackRepository feedbackRepository;

    private User user;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setName("test");
        user.setPasswordHash("hashyhash");
        user.setEmail("test@quacker.com");
    }

    @Test
    @DisplayName("Feedback should be received")
    @WithMockUser(username = "test", password = "pwd", roles = "USER")
    public void postFeedbackTest() throws Exception {
        when(accountService.currentUser()).thenReturn(user);
        mockMvc
                .perform(post("/help/feedback")
                        .with(csrf())
                        .param("category", "BUG")
                        .param("header", "vituiksmän")
                        .param("message", "yritin quackkaa mut ei toiminu"));

        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }

    @Test
    @DisplayName("Should load page")
    @WithMockUser(username = "test", password = "pwd", roles = "USER")
    public void getFeedbackTest() throws Exception {
        when(accountService.currentUser()).thenReturn(user);
        mockMvc
                .perform(get("/help/feedback"))
                .andExpect(status().is2xxSuccessful());
    }
}
