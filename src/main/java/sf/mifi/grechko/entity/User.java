package sf.mifi.grechko.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sf.mifi.grechko.dto.UserRole;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String passwd;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "role")
    private UserRole role;

    @Column(nullable = false)
    private String telegram;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;
}
