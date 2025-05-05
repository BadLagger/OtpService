package sf.mifi.grechko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sf.mifi.grechko.dto.UserRole;
import sf.mifi.grechko.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByRole(UserRole role);

    User findByLogin(String login);

    @Query("SELECT u.role FROM User u WHERE u.login = :login")
    UserRole findRoleByLogin(@Param("login") String login);
}
