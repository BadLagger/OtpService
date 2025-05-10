package sf.mifi.grechko.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WrongConfigParameter extends ResponseStatusException {
    public WrongConfigParameter(String message) {
        super(HttpStatus.FAILED_DEPENDENCY, message);
    }
}
