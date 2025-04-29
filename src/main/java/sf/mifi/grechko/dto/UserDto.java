package sf.mifi.grechko.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(
    Long     id,
    String   login,
    String   passwd,
    UserRole role,
    String   telegram,
    String   email,
    @Pattern(regexp = "^\\+(?:\\d[- ]?){6,14}\\d$", message = "Wrong number format.")
    String   phone
) { }
