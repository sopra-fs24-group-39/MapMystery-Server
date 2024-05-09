package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class PasswordValidatorTest {

    @Mock
    User user;

    @InjectMocks
    UserService userService;

    @Test
    public void testValidPassword() throws Exception{
        when(user.getPassword()).thenReturn("Password123!");
        userService.checkPassword(user); // Should not throw an exception
    }

    @Test
    public void testInvalidPasswordMissingUppercase() {
        when(user.getPassword()).thenReturn("password123!");
        assertThrows(ResponseStatusException.class, () -> userService.checkPassword(user),
                "The password doesn't contain an Upper Case Letter [A-Z]");
    }

    @Test
    public void testInvalidPasswordMissingNumerical() {
        when(user.getPassword()).thenReturn("Password!");
        assertThrows(ResponseStatusException.class, () -> userService.checkPassword(user),
                "The password doesn't contain a numerical character [0-9]");
    }

    @Test
    public void testInvalidPasswordMissingSpecialChar() {
        when(user.getPassword()).thenReturn("Password123");
        assertThrows(ResponseStatusException.class, () -> userService.checkPassword(user),
                "The password doesn't contain special Character.");
    }
}
