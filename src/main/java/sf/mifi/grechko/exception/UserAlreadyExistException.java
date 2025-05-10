package sf.mifi.grechko.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyExistException extends ResponseStatusException {
    public UserAlreadyExistException(UserExceptionType ex) {
        super(HttpStatus.CONFLICT, "User with the same " + ex.getDescription() + " already exists!");
    }
}
