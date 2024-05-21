package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.rest.dto.SettingsPutDTO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsPutDTOTest {

    @Test
    public void testPassword() {
        SettingsPutDTO dto = new SettingsPutDTO();
        String password = "password123";
        dto.setPassword(password);
        assertEquals(password, dto.getPassword());
    }

    @Test
    public void testUsername() {
        SettingsPutDTO dto = new SettingsPutDTO();
        String username = "username";
        dto.setUsername(username);
        assertEquals(username, dto.getUsername());
    }


    @Test
    public void testUserEmail() {
        SettingsPutDTO dto = new SettingsPutDTO();
        String email = "user@example.com";
        dto.setUserEmail(email);
        assertEquals(email, dto.getUserEmail());
    }
}