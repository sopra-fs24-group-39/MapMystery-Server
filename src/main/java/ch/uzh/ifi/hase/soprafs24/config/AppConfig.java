package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  // Disabled for deployment
    // @Value("${app.key:#{null}}")
    // private String secretKey;

    // public String getSecretKey() {
    //     if (secretKey == null) {
    //         secretKey = System.getenv("APP_KEY");
    //     }
    //     return secretKey;
    // }
}
