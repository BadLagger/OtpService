package sf.mifi.grechko.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import sf.mifi.grechko.dto.*;
import sf.mifi.grechko.entity.User;
import sf.mifi.grechko.service.JwtService;
import sf.mifi.grechko.service.OtpService;
import sf.mifi.grechko.service.UserService;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws AccessDeniedException {

        User user = userService.login(request);

        String accessToken = jwtService.generateAccessToken(user.getLogin());
        String refreshToken = jwtService.generateRefreshToken(user.getLogin());

        Date accessExp = jwtService.getExpiration(accessToken);
        Date refreshExp = jwtService.getExpiration(refreshToken);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUsername(user.getLogin());
        response.setRole(user.getRole());
        response.setAccessExpiresAt(accessExp.toInstant());
        response.setRefreshExpiresAt(refreshExp.toInstant());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        log.info("Try to register: {}, {}", request.getName(), request.getRole());

        userService.registerUser(request);

        return ResponseEntity.ok("User:  " + request.getName() + " registration success");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        String refreshToken = request.refreshToken();

        String username = jwtService.extractUsername(refreshToken);

        if (!jwtService.isTokenValid(refreshToken, username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not valid refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(username);
        Instant newAccessExp = jwtService.getExpiration(newAccessToken).toInstant();

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("accessExpiresAt", newAccessExp);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("No token");
        }

        String token = authHeader.substring(7);
        jwtService.blacklist(token);

        return ResponseEntity.ok("User exit");
    }
}
