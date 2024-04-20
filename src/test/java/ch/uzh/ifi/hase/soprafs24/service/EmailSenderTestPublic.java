package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("Live test to live email")
@SpringBootTest
@TestPropertySource(locations="classpath:test.properties") // Load test properties
public class EmailSenderTestPublic {

    @Autowired
    private AccountService accountService; // Inject AccountService under test

    @Value("${app.base.url}")
    private String baseUrl;

    @Test
    public void testSendVerificationEmail() {
        // Given
        User user = new User();
        user.setUserEmail("wallacees12@gmail.com");

        // When
        accountService.sendVerificationEmail(user);

        // Then
        // Verify that a unique verification token was generated and associated with the user
        assertEquals(36, user.getVerificationToken().length()); // Verify token length

        // Since we are using the real EmailSenderService, the actual email sending process will be executed.
        // You may verify the email delivery manually using the provided email address.
    }
}
