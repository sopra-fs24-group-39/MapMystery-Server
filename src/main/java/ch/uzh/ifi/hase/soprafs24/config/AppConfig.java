package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${app.key}")
    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }
}
