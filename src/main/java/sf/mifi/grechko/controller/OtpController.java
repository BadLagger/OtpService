package sf.mifi.grechko.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sf.mifi.grechko.dto.UserDto;
import sf.mifi.grechko.dto.UserRole;
import sf.mifi.grechko.entity.User;
import sf.mifi.grechko.service.OtpService;
import sf.mifi.grechko.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OtpController {

    public final OtpService otpService;
    public final UserService userService;

    public OtpController(OtpService otpService, UserService userService) {
        this.otpService = otpService;
        this.userService = userService;
    }

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

            model.addAttribute("username", username);
            model.addAttribute("role", role.toString());
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

    // Обработчик ошибок
    @ExceptionHandler(IllegalArgumentException.class)
    protected String handleIllegalArguments(IllegalArgumentException ex, Model model) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        model.addAttribute("errors", errors);
        return "validation-error";
    }

    // Обработчик ошибок
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected String handleValidationErrors(MethodArgumentNotValidException ex, Model model) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        List<String> errors = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        model.addAttribute("errors", errors); // Передаем ошибки в модель
        return "validation-error";
    }
}
