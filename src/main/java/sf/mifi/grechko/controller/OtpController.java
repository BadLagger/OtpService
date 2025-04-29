package sf.mifi.grechko.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sf.mifi.grechko.service.OtpService;

@RestController
public class OtpController {

    public final OtpService service;

    public OtpController(OtpService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserDto request) {
        throw new IllegalArgumentException("Bad User");
    }
}
