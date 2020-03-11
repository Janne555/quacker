package com.group5.quacker.services;

import com.group5.quacker.controllers.SettingsController;
import com.group5.quacker.entities.Quack;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.QuackRepository;
import com.group5.quacker.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {"QUACKER_FILE_STORAGE = foo"})
public class AccountServiceTests {
    @Autowired
    private FileService fileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private QuackRepository quackRepository;

    @BeforeEach
    public void beforeEach() {
        // add user
        User userJohn = new User();
        userJohn.setName("john");
        userJohn.setPasswordHash("hashyhash_john");
        userJohn.setEmail("john@quacker.com");
        userJohn.setFollowers(new ArrayList<>());
        userJohn.setFollowing(new ArrayList<>());
        userJohn.setQuacks(new ArrayList<>());
        userRepository.save(userJohn);
        userRepository.flush();

        // add another user
        User userJane = new User();
        userJane.setName("jane");
        userJane.setPasswordHash("hashyhash_jane");
        userJane.setEmail("jane@quacker.com");
        userJane.setFollowers(new ArrayList<>());
        userJane.setFollowing(new ArrayList<>());
        userRepository.save(userJane);
        userRepository.flush();

        // add quack
        Quack johnsQuack = new Quack();
        johnsQuack.setPoster(userJohn);
        johnsQuack.setLikers(new ArrayList<>());
        johnsQuack.setQuackMessage("moi");
        johnsQuack.addLiker(userJane);
        userJohn.addQuack(johnsQuack);
        quackRepository.save(johnsQuack);
        userRepository.save(userJohn);
        userRepository.save(userJane);
        userRepository.flush();

        // set followers
        userJohn.addFollower(userJane);
        userJane.addFollowing(userJohn);
        userRepository.save(userJane);
        userRepository.save(userJohn);
        userRepository.flush();
    }

    @Test
    @Transactional
    public void testAccountIsDeleted() {
        User john = userRepository.findByName("john");
        accountService.deleteAccount(john);
        User jane = userRepository.findByName("jane");
        assertEquals(0, quackRepository.findAll().size());
        assertEquals(1, userRepository.findAll().size());
        assertEquals(0, jane.getFollowers().size());
        assertEquals(0, jane.getFollowing().size());
    }
}
