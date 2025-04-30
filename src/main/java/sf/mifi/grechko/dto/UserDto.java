package sf.mifi.grechko.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(
    Long     id,
    @NotBlank(message = "Login is empty")
    String   login,
    @NotBlank(message = "Password is empty")
    String   passwd,
    UserRole role,
    @NotBlank(message = "Telegram is empty")
    String   telegram,
    @NotBlank(message = "Email is empty")
    String   email,
    @Pattern(regexp = "^\\+(?:\\d[- ]?){6,14}\\d$", message = "Wrong number format.")
    String   phone
) { }
