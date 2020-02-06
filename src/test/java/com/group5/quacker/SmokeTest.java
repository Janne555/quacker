package com.group5.quacker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"QUACKER_FILE_STORAGE = foo"})
public class SmokeTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldStartWebApp() throws Exception {
        assertThat(mockMvc).isNotNull();
    }
}
