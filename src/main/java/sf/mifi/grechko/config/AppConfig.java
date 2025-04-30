package sf.mifi.grechko.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.security.Key;
import java.security.SecureRandom;


@Configuration
public class AppConfig {

    private byte[] secKey = null;

    @Bean
    public byte[] secretKey() {
        if (secKey == null) {
            SecureRandom random = new SecureRandom();
            secKey = new byte[32];
            random.nextBytes(secKey);
        }
        return secKey;
    }
}
