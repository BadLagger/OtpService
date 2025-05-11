package sf.mifi.grechko.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    @NotNull(message = "Username is mandatory")
    private String name;
    @NotNull(message = "Password is mandatory")
    private String password;

    @NotNull(message = "Role is  mandatory")
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @NotNull(message = "Telegram Id is mandatory")
    private String telegram;
    @NotNull(message = "Email Id is mandatory")
    private String email;
    @NotNull(message = "Phone Id is mandatory")
    private String phone;
}