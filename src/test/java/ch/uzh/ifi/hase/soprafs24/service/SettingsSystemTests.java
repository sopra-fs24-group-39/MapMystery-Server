package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SettingsSystemTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
/*
    @Test
    public void updateUserSettings_changeUsername_success() throws Exception {
        // Implement your test case here
    }

    @Test
    public void updateUserSettings_changeEmail_success() throws Exception {
        // Implement your test case here
    }

    @Test
    public void updateUserSettings_duplicateUsername() {
        // Implement your test case here
    }

    @Test
    public void updateUserSettings_duplicateEmail() {
        // Implement your test case here
    }*/

    @Test
    public void updateUserSettings_usernameExists() throws Exception {
        User existingUser = new User();
        existingUser.setUsername("existingUser");

        User userToUpdate = new User();
        userToUpdate.setUsername("existingUser"); // Same username as existingUser

        when(userRepository.findByUsername("existingUser")).thenReturn(existingUser);

        assertThrows(ResponseStatusException.class, () -> userService.updateUserSettings(existingUser, userToUpdate));
    }

    @Test
    public void updateUserSettings_emailExists() throws Exception {
        User existingUser = new User();
        existingUser.setUsername("existingUser");
        existingUser.setUserEmail("existing@example.com");

        User userToUpdate = new User();
        userToUpdate.setUserEmail("existing@example.com"); // Same email as existingUser

        when(userRepository.findByUserEmail("existing@example.com")).thenReturn(existingUser);

        assertThrows(ResponseStatusException.class, () -> userService.updateUserSettings(existingUser, userToUpdate));
    }
}