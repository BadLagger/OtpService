package sf.mifi.grechko.dto;

import jakarta.validation.constraints.NotBlank;

public record OtpRequestDto(
        @NotBlank(message = "Login is empty")
        String login,
        @NotBlank(message = "Type is empty")
        String type
) { }
