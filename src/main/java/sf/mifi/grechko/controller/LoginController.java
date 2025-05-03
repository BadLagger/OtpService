package sf.mifi.grechko.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import sf.mifi.grechko.dto.UserDto;
import sf.mifi.grechko.dto.UserRole;
import sf.mifi.grechko.service.JwtService;
import sf.mifi.grechko.service.OtpService;
import sf.mifi.grechko.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class LoginController {

    public final OtpService otpService;
    public final UserService userService;
    public final JwtService jwtService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Имя шаблона (без расширения .html)
    }

    @PostMapping("login-check")
    public String processLogin(@RequestParam String username, @RequestParam String passwd, Model model) {
        System.out.println("ProcessLogin");
        try {
            // Попытайтесь аутентифицировать пользователя
            UserRole role = userService.authenticate(username, passwd);

            // Генерируем JWT-токен
            String jwtToken = jwtService.generateToken(username);

            model.addAttribute("username", username);
            model.addAttribute("role", role.toString());
            model.addAttribute("jwtToken", jwtToken);

            // Аутентификация прошла успешно, перенаправляем на домашнюю страницу
            return "userpage";
        } catch (AuthenticationException e) {
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            // Аутентификация провалилась, передаем ошибку в шаблон
            System.out.println("LoginERR!");
            //redirectAttrs.addFlashAttribute("error", "Invalid credentials or account locked.");
            model.addAttribute("errors", errors);
            return "validation-error";
        }
    }

    @GetMapping("/register-form")
    public String showRegistrationForm(Model model) {
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute UserDto request, Model model) throws MethodArgumentNotValidException {

        userService.registerNewUser(request);
        model.addAttribute("message", "Registration OK!");
        return "success";
    }
}
