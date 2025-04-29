package sf.mifi.grechko.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sf.mifi.grechko.dto.UserDto;
import sf.mifi.grechko.entity.User;
import sf.mifi.grechko.service.OtpService;

@Controller
public class OtpController {

    public final OtpService service;

    public OtpController(OtpService service) {
        this.service = service;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Имя шаблона (без расширения .html)
    }

    @GetMapping("/register-form")
    public String showRegistrationForm(Model model) {
        return "registration";
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @ModelAttribute UserDto request) {
        System.out.println("Get in register");
        throw new IllegalArgumentException("Bad User");
    }

    // Обработчик ошибок
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArguments(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
