package sf.mifi.grechko.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoEntityFoundException extends ResponseStatusException {
    public NoEntityFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
