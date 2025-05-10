package sf.mifi.grechko.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sf.mifi.grechko.entity.OtpConfig;

@Repository
public interface OtpConfigRepository extends JpaRepository<OtpConfig, Long> {
}
