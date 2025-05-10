package sf.mifi.grechko.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadOtpType extends ResponseStatusException {
    public BadOtpType(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
