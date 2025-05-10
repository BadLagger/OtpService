package sf.mifi.grechko.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sf.mifi.grechko.dto.OtpConfigDto;
import sf.mifi.grechko.dto.UserRole;
import sf.mifi.grechko.entity.OtpConfig;
import sf.mifi.grechko.entity.User;
import sf.mifi.grechko.mapper.OtpConfigMapper;
import sf.mifi.grechko.service.OtpService;
import sf.mifi.grechko.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OtpConfigMapper otpConfigMapper;
    private final UserService userService;
    private final OtpService otpService;

    @GetMapping("/users")
    public List<User> getAllUsers(){
        return userService.getAllUsers(UserRole.USER);
    }

    @PutMapping("/del/{user_id}")
    public ResponseEntity<?> delUserById(@PathVariable("user_id") Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok("User deleted successful");
    }

    @GetMapping("/otp/config")
    public OtpConfigDto getOtpConfig() {
        return otpConfigMapper.toDto(otpService.getConfig());
    }

    @PutMapping("/otp/config")
    public ResponseEntity<?>  putOtpConfig(@Valid @RequestBody OtpConfigDto config) {

        log.info("Try to set new OTP config: lifetime {}, sign number {}", config.lifetime(), config.signNumber());

        var otpCfg = otpConfigMapper.toEntity(config);

        log.debug("After mapper: {} {}", otpCfg.getLifetime(), otpCfg.getSignNumber());

        otpService.setConfig(otpCfg);

        return ResponseEntity.ok("Otp config set successful");
    }
}
