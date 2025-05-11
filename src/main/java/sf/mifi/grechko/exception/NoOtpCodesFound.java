package sf.mifi.grechko.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoOtpCodesFound extends ResponseStatusException {
    public NoOtpCodesFound(String message) {
      super(HttpStatus.NOT_FOUND, message);
    }
}
