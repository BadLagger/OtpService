package sf.mifi.grechko.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(
    Long     id,
    String   login,
    String   passwd,
    UserRole role,
    String   telegram,
    String   email,
    String   phone
) { }
