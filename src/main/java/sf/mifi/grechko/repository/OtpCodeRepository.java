package sf.mifi.grechko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sf.mifi.grechko.dto.OtpStatus;
import sf.mifi.grechko.entity.OtpCode;
import sf.mifi.grechko.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<List<OtpCode>> findByStatus(OtpStatus otpStatus);

    Optional<List<OtpCode>> findByUser(User user);

    @Query("SELECT o FROM OtpCode o WHERE o.user.id = :userId AND o.status = :status")
    Optional<List<OtpCode>> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") OtpStatus status);
}
