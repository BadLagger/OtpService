package sf.mifi.grechko.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sf.mifi.grechko.dto.UserRole;
import sf.mifi.grechko.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByRole(UserRole role);

    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByPhone(@NotNull(message = "Phone Id is mandatory") String phone);

    boolean existsByTelegram(@NotNull(message = "Telegram Id is mandatory") String telegram);

    boolean existsByEmail(@NotNull(message = "Email Id is mandatory") String email);

    Optional<List<User>> findByRole(UserRole userRole);


}
