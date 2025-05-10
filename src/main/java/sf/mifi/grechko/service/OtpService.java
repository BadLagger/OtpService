package sf.mifi.grechko.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sf.mifi.grechko.dto.UserDto;
import sf.mifi.grechko.dto.UserRole;
import sf.mifi.grechko.entity.OtpConfig;
import sf.mifi.grechko.entity.User;
import sf.mifi.grechko.exception.WrongConfigParameter;
import sf.mifi.grechko.mapper.UserMapper;
import sf.mifi.grechko.repository.OtpConfigRepository;
import sf.mifi.grechko.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    //private final UserRepository userRepository;
    private final OtpConfigRepository otpConfigRepository;

    @Value("${default.otp.config.lifetime}")
    private Long otpConfigLifetimeDefault;
    @Value("${default.otp.config.sign-number}")
    private Long otpConfigSignNumberDefault;

    private void setOtpConfig(Long lifetime, Long signNumber) {

        log.info("Set new OTP config with: lifetime - {}, sign number - {}", lifetime, signNumber);
        if (otpConfigRepository.count() > 0) {
            log.warn("Try to delete old OTP config");
            otpConfigRepository.deleteAll();
            log.warn("Old OTP config deleted");
        }

        OtpConfig defaultOtpConfig = new OtpConfig();
        defaultOtpConfig.setLifetime(lifetime);
        defaultOtpConfig.setSignNumber(signNumber);
        log.info("Try to set new OTP config");
        otpConfigRepository.save(defaultOtpConfig);
        log.info("New OTP config set");
    }


    @PostConstruct
    public void init() {
        log.info("Init Otp Config");

        // Если больше одного конфига при старте, то грохнуть всё
        var otpConfigNumber = otpConfigRepository.count();
        log.info("Number of config in DB: {}", otpConfigNumber);
        if (otpConfigNumber > 1) {
            log.error("More than one OTP Configs found in DB. Delete all");
           // otpConfigRepository.deleteAll();
            log.info("All OTP configs deleted");
        }

        // Можно ещё проверок всяких нагородить на адекватность конфига

        // Если нет конфига вообще, то установить конфиг по умолчанию
        if (otpConfigRepository.count() == 0) {
            log.error("No OTP config in DB. Try to create OTP config by default.");
            setOtpConfig(otpConfigLifetimeDefault, otpConfigSignNumberDefault);
        }

        log.info("Load OTP Config from DB");
        OtpConfig config = getConfig();
        log.info("Current OTP Config: lifetime - {}, sign number - {}", config.getLifetime(), config.getSignNumber());
        log.info("Init Otp Config DONE!!!");
    }

    public OtpConfig getConfig() {
        log.debug("Try to read config from DB");
        var config = otpConfigRepository.findAll();

        log.debug("Read done!!!");
        if (config.size() != 1) {
            throw new WrongConfigParameter("Wrong OTP Config");
        }

        return config.get(0);
    }

    public void setConfig(OtpConfig config) {
        setOtpConfig(config.getLifetime(), config.getSignNumber());
    }
}
