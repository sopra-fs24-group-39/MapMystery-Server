package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private UserService userService; // Assuming you have a UserService for managing users

    @Autowired
    private EmailSenderService emailSenderService; // Assuming you have an EmailSenderService for sending emails

    @Value("${app.base.url}") // Read the base URL from application.properties
    private String baseUrl;

    public void sendVerificationEmail(User user) {
        // Generate a unique verification token
        String verificationToken = UUID.randomUUID().toString();

        // Associate the token with the user
        user.setVerificationToken(verificationToken);

        // Construct the verification link
        String verificationLink = baseUrl + "/verify-account?token=" + verificationToken;

        // Send the verification link to the user via email
        String emailBody = "Hi!\n" +
                "Welcome to MapMystery! We are excited to have you onboard with the Geography learning Journey, " +
                "we have many exciting game modes for you to try but first.....\n" +
                "Please click the following link to verify your account: " + verificationLink;
        emailSenderService.sendEmail(user.getUserEmail(), "Account Verification", emailBody);


    }

}
