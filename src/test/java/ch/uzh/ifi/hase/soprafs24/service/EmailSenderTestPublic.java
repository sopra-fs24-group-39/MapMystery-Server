package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@TestPropertySource(locations="classpath:test.properties") // Load test properties
public class EmailSenderTestPublic {

    @Autowired
    private AccountService accountService; // Inject AccountService under test

    @Value("${app.base.url}")
    private String baseUrl;

    @Disabled("Live test to live email")
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

    @Mock
    private JavaMailSender mailSender; // Mock the JavaMailSender

    @InjectMocks
    private EmailSenderService emailSenderService; // Inject mocks into the email service

    @Test
    public void sendEmailTest() {
        // Arrange
        String toEmail = "test@example.com";
        String subject = "Test Subject";
        String body = "Hello, this is a test email.";

        // Act
        emailSenderService.sendEmail(toEmail, subject, body);

        // Assert
        verify(mailSender).send(argThat((SimpleMailMessage message) ->
                message.getTo()[0].equals(toEmail) &&
                        message.getSubject().equals(subject) &&
                        message.getText().equals(body) &&
                        message.getFrom().equals("mapmysterysopra@gmail.com")
        ));
    }
}
