package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;


import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @Mock
    private UserService userService; // Mock UserService dependency

    @Mock
    private EmailSenderService emailSenderService; // Mock EmailSenderService dependency

    @Value("${app.base.url}")
    private String baseUrl;

    @InjectMocks
    private AccountService accountService; // Inject mocks into AccountService under test

    public AccountServiceTest() {
        MockitoAnnotations.initMocks(this); // Initialize mocks
    }
    private int int2;

    @Test
    public void testSendVerificationEmail() {
        // Given
        User user = new User();
        user.setUserEmail("test@example.com");

        // When
        accountService.sendVerificationEmail(user);

        // Then
        // Verify that a unique verification token was generated and associated with the user
        assertEquals(36, user.getVerificationToken().length()); // Verify token length

        // Verify that the verification link is constructed correctly
        String expectedVerificationLink = "Hi!\n" +
                "Welcome to MapMystery! We are excited to have you onboard with the Geography learning Journey, " +
                "we have many exciting game modes for you to try but first.....\n" +
                "Please click the following link to verify your account: " + "https://sopra-fs24-group-39-client.oa.r.appspot.com" + "/verify-account?token=" + user.getVerificationToken();
        assertEquals(expectedVerificationLink, captureEmailBody());

        // Verify that the email was sent to the user's email address with the correct subject and body
        verify(emailSenderService, times(1)).sendEmail(eq("test@example.com"), eq("Account Verification"), anyString());
    }

    // Helper method to capture the email body argument passed to emailSenderService.sendEmail
    private String captureEmailBody() {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailSenderService).sendEmail(anyString(), anyString(), argumentCaptor.capture());
        return argumentCaptor.getValue();
    }
}
