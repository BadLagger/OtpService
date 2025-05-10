package sf.mifi.grechko.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WrongPermissionRights extends ResponseStatusException {
    public WrongPermissionRights(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
