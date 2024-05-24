package ch.uzh.ifi.hase.soprafs24.Entities;


import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User originalUser;
    private User newUserData;

    @BeforeEach
    void setUp() {
        originalUser = new User();
        originalUser.setUsername("originalUsername");
        originalUser.setPasswordAlreadyEncoded("originalPassword");
        originalUser.setStatus("originalStatus");
        originalUser.setUserEmail("original@example.com");
        originalUser.setFeatured_in_rankings(true);
        originalUser.setAccept_friendrequests(true);
        originalUser.setProfilepicture(1);
        originalUser.setVerified(true);

        newUserData = new User();
        newUserData.setUsername("newUsername");
        newUserData.setPasswordAlreadyEncoded("newPassword");
        newUserData.setStatus("newStatus");
        newUserData.setUserEmail("new@example.com");
        newUserData.setFeatured_in_rankings(false);
        newUserData.setAccept_friendrequests(false);
        newUserData.setProfilepicture(2);
        newUserData.setVerified(false);
    }

    @Test
    void testUpdate() {
        originalUser.update(newUserData);

        assertEquals("newUsername", originalUser.getUsername());
        assertEquals("newPassword", originalUser.getPassword());
        assertEquals("newStatus", originalUser.getStatus());
        assertEquals("new@example.com", originalUser.getUserEmail());
        assertFalse(originalUser.getFeatured_in_rankings());
        assertFalse(originalUser.getAccept_friendrequests());
        assertEquals(2, originalUser.getProfilepicture());
        assertTrue(originalUser.getVerified());
    }
}
