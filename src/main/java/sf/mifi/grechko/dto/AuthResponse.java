package sf.mifi.grechko.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private UserRole role;
    private Instant accessExpiresAt;
    private Instant refreshExpiresAt;
}

