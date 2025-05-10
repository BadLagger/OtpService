package sf.mifi.grechko.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sf.mifi.grechko.dto.OtpConfirm;
import sf.mifi.grechko.dto.OtpType;
import sf.mifi.grechko.exception.BadOtpType;
import sf.mifi.grechko.service.OtpService;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final OtpService otpService;

    @GetMapping("/otp/{otp_type}")
    public ResponseEntity<?> getOtpRequest(@PathVariable("otp_type")String otpString) {

        OtpType otpType;

        try {
            otpType = OtpType.valueOf(otpString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadOtpType("Unsupported type of OTP");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        otpService.createOtp(auth.getName(), otpType);
        return ResponseEntity.ok("OTP created. Please confirm by PUT request!");
    }

    @PutMapping("/otp")
    public ResponseEntity<?> putOtpRequest(@Valid @RequestBody OtpConfirm otpConfig) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        otpService.checkOtp(auth.getName(), otpConfig.getNumbers());
        return ResponseEntity.ok("OTP confirmed successful");
    }
}
