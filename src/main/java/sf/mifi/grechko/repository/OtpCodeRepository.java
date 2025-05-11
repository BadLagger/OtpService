package sf.mifi.grechko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sf.mifi.grechko.dto.OtpStatus;
import sf.mifi.grechko.entity.OtpCode;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<List<OtpCode>> findByStatus(OtpStatus otpStatus);
}
