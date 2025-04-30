package sf.mifi.grechko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sf.mifi.grechko.dto.UserRole;
import sf.mifi.grechko.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //  Все эьти проверки не нужны так как эти поля в БД уникальны
    //   boolean existsByLogin(String login);
    //   boolean existsByEmail(String email);
    //   boolean existsByTelegram(String telegram);
    //   boolean existsByPhone(String phone);
    boolean existsByRole(UserRole role);

    User findByLogin(String login);
}
