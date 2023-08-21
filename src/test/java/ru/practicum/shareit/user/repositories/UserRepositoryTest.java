package ru.practicum.shareit.user.repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {
    public static final String EXIST_EMAIL = "test@email.com";

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(User.builder()
                .name("test")
                .email(EXIST_EMAIL)
                .build());

        userRepository.save(User.builder()
                .name("test1")
                .email("test2@email.com")
                .build());
    }

    @Test
    void existsUserByIdIs_thenInvoked_thenReturnTrue() {
        List<User> all = userRepository.findAll();
        assertTrue(userRepository.existsUserByIdIs(all.get(0).getId()));
    }

    @Test
    void existsUserByIdIs_thenInvokedUserNotExist_thenReturnFalse() {
        assertFalse(userRepository.existsUserByIdIs(0L));
    }

    @Test
    void deleteUserByIdIs_whenInvoked_thenSuccess() {
        userRepository.deleteUserByIdIs(2L);
        assertFalse(userRepository.existsUserByIdIs(2L));
    }

    @Test
    void existsUserByEmailIs_whenInvoked_thenTrue() {
        assertTrue(userRepository.existsUserByEmailIs(EXIST_EMAIL));
    }

    @Test
    void existsUserByEmailIs_whenInvokedNotExistEmail_thenFalse() {
        assertFalse(userRepository.existsUserByEmailIs("test1@email.com"));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }
}