import ch.uzh.ifi.hase.soprafs24.rest.dto.SettingsPutDTO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsPutDTOTest {

    @Test
    public void testId() {
        SettingsPutDTO dto = new SettingsPutDTO();
        Long id = 1L;
        dto.setId(id);
        assertEquals(id, dto.getId());
    }

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
    public void testStatus() {
        SettingsPutDTO dto = new SettingsPutDTO();
        String status = "ONLINE";
        dto.setUserStatus(status);
        assertEquals(status, dto.getStatus());
    }

    @Test
    public void testCreationDate() {
        SettingsPutDTO dto = new SettingsPutDTO();
        String creationDate = "2023-01-01";
        dto.setCreationdate(creationDate);
        assertEquals(creationDate, dto.getCreationdate());
    }

    @Test
    public void testUserEmail() {
        SettingsPutDTO dto = new SettingsPutDTO();
        String email = "user@example.com";
        dto.setUserEmail(email);
        assertEquals(email, dto.getUserEmail());
    }
}