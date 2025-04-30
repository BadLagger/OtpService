package sf.mifi.grechko.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sf.mifi.grechko.dto.UserDto;
import sf.mifi.grechko.dto.UserRole;
import sf.mifi.grechko.entity.User;
import sf.mifi.grechko.mapper.UserMapper;
import sf.mifi.grechko.repository.UserRepository;

@Service
public class OtpService {

    @Autowired
    private UserRepository userRepository;

}
