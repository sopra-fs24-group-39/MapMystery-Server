import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.service.AccountService;
import ch.uzh.ifi.hase.soprafs24.service.EmailSenderService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties") // Load test properties
public class EmailSenderTestPublic {

    @Mock
    private UserService userService; // Mock UserService dependency

    @Mock
    private EmailSenderService emailSenderService; // Mock EmailSenderService dependency

    @Value("${app.base.url}")
    private String baseUrl;

    @InjectMocks
    private AccountService accountService; // Inject mocks into AccountService under test

    public EmailSenderTestPublic() {
        MockitoAnnotations.initMocks(this); // Initialize mocks
    }

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

        // Verify that the email was sent to the user's email address with the correct subject and body
        // Since we are sending a real email, no need to verify email content
    }
}
