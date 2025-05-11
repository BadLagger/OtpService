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
import sf.mifi.grechko.dto.OtpStatus;
import sf.mifi.grechko.dto.OtpType;
import sf.mifi.grechko.dto.UserDto;
import sf.mifi.grechko.dto.UserRole;
import sf.mifi.grechko.entity.OtpCode;
import sf.mifi.grechko.entity.OtpConfig;
import sf.mifi.grechko.entity.User;
import sf.mifi.grechko.exception.WrongConfigParameter;
import sf.mifi.grechko.mapper.UserMapper;
import sf.mifi.grechko.repository.OtpCodeRepository;
import sf.mifi.grechko.repository.OtpConfigRepository;
import sf.mifi.grechko.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserRepository userRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final OtpConfigRepository otpConfigRepository;

    @Value("${default.otp.config.lifetime}")
    private Long otpConfigLifetimeDefault;
    @Value("${default.otp.config.sign-number}")
    private Long otpConfigSignNumberDefault;
    @Value("${default.otp.max-pool-size}")
    private Integer maxPoolSize;

    private ScheduledExecutorService executorOtpCodeExpired = null;
    private ConcurrentHashMap<Long, ScheduledFuture<?>> sсheduledOtpCodeExpired = null;

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

    private OtpCode generateOptCode(User user, OtpConfig config) {
        OtpCode result = new OtpCode();
        List<Integer> code = new ArrayList<>();
        Random random = new Random();

        while (code.size() < config.getSignNumber()) {
            code.add(random.nextInt(10));
        }

        result.setUser(user);
        result.setCode(code);
        result.setRequestTime(LocalDateTime.now());
        result.setStatus(OtpStatus.ACTIVE);
        return result;
    }

    private void setToActive(OtpCode code, OtpConfig config) {
        log.info("Set OTP to Active");
        if (code.getId() == null || !otpCodeRepository.existsById(code.getId())) {
            log.debug("OtpCode with id = {} is not exists", code.getId());
            log.info("Try to save new ACTIVE OTP");
            otpCodeRepository.save(code);
            log.debug("New OTP saved");
        }

        log.debug("Try to add OTP to the  expired executor");
        long delayMs = ChronoUnit.MILLIS.between(LocalDateTime.now(), code.getRequestTime().plusMinutes(config.getLifetime()));
        try {
            var future = executorOtpCodeExpired.schedule(() -> setToExpired(code), delayMs, TimeUnit.MILLISECONDS);
            sсheduledOtpCodeExpired.put(code.getId(), future);
        } catch (Exception ex) {
            log.error("Cannot add OTP to the expired executor. Delete from DB. More info: {}", ex.getMessage());
            otpCodeRepository.delete(code);
            return;
        }
        log.debug("Add to OTP to the expired executor DONE!!!");
        log.info("OTP setting to the Active DONE!!!!");
    }

    private void setToExpired(OtpCode code) {
        log.info("Set OTP to Expired");
        code.setStatus(OtpStatus.EXPIRED);
        otpCodeRepository.save(code);
        sсheduledOtpCodeExpired.remove(code.getId());
        log.info("OTP setting to the Expired DONE!!!!");
    }

    private void checkActiveCodes(OtpConfig config) {
        var codes = otpCodeRepository.findByStatus(OtpStatus.ACTIVE);

        if (codes.isPresent()) {
            for (var code : codes.get()) {
                var expiredOtpTime = code.getRequestTime().plusMinutes(config.getLifetime());
                if (expiredOtpTime.isBefore(LocalDateTime.now())) {
                    setToActive(code, config);
                } else {
                    setToExpired(code);
                }
            }
        }
    }


    @PostConstruct
    public void init() {
        log.info("Init Otp Config");

        // Если больше одного конфига при старте, то грохнуть всё
        var otpConfigNumber = otpConfigRepository.count();
        log.info("Number of config in DB: {}", otpConfigNumber);
        if (otpConfigNumber > 1) {
            log.error("More than one OTP Configs found in DB. Delete all");
            otpConfigRepository.deleteAll();
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

        log.info("OTP Executor init");
        executorOtpCodeExpired  = Executors.newScheduledThreadPool(maxPoolSize);
        sсheduledOtpCodeExpired = new ConcurrentHashMap<>();
        log.info("Checking existing OTP...");
        checkActiveCodes(config);
        log.info("Checking existing OTP DONE!!!");
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

    public void createOtp(String name, OtpType otpType) {
        var user = userRepository.findByLogin(name);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Not exist user with login " + name);
        }

        OtpConfig config = getConfig();
        OtpCode code = generateOptCode(user.get(), config);
        setToActive(code, config);
        // todo send Message To the messanger

    }

    public void checkOtp(String name, List<Integer> numbers) {
        // todo
    }
}
