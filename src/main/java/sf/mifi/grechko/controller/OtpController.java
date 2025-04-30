package sf.mifi.grechko.controller;

import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sf.mifi.grechko.dto.UserDto;
import sf.mifi.grechko.entity.User;
import sf.mifi.grechko.service.OtpService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping("login-check")
    public String processLogin(@RequestParam String username, @RequestParam String passwd, Model model) {
        System.out.println("ProcessLogin");
        try {
            // Попытайтесь аутентифицировать пользователя
            service.authenticate(username, passwd);
            System.out.println("LoginOK!");
            model.addAttribute("username", username);
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
        /*System.out.println("Get in register");
        System.out.format("Login: %s\n", request.login());
        System.out.format("Password: %s\n", request.passwd());
        System.out.format("email: %s\n", request.email());
        System.out.format("role: %s\n", request.role());
        System.out.format("telega: %s\n", request.telegram());
        System.out.format("phone: %s\n", request.phone());*/

        service.registerNewUser(request);
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
