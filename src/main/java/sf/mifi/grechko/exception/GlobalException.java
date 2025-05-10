package sf.mifi.grechko.exception;

import io.jsonwebtoken.JwtException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(NoEntityFoundException.class)
    public ResponseEntity<String> handleNoEntityFound(NoEntityFoundException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwtException(JwtException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
    // Обработчик ошибок
   /* @ExceptionHandler(IllegalArgumentException.class)
    protected String handleIllegalArguments(IllegalArgumentException ex, Model model) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        model.addAttribute("errors", errors);
        return "validation-error";
    }*/

    // Обработчик ошибок
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<String>  handleValidationErrors(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(WrongPermissionRights.class)
    protected ResponseEntity<String> handleWrongPermissions(WrongPermissionRights ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }

    @ExceptionHandler(WrongConfigParameter.class)
    protected ResponseEntity<String> handleWrongPermissions(WrongConfigParameter ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }

    @ExceptionHandler(BadOtpType.class)
    protected ResponseEntity<String> handleBadOtpType(BadOtpType ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
    }
}
