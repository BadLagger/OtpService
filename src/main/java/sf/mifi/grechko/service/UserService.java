package sf.mifi.grechko.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sf.mifi.grechko.dto.LoginRequest;
import sf.mifi.grechko.dto.RegisterRequest;
import sf.mifi.grechko.dto.UserDto;
import sf.mifi.grechko.dto.UserRole;
import sf.mifi.grechko.entity.User;
import sf.mifi.grechko.exception.UserAlreadyExistException;
import sf.mifi.grechko.exception.UserExceptionType;
import sf.mifi.grechko.mapper.UserMapper;
import sf.mifi.grechko.repository.UserRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService{
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public void registerUser(RegisterRequest newUser) {
        if (userRepository.existsByLogin(newUser.getName())) {
            log.error("User {} already exists", newUser.getName());
            throw new UserAlreadyExistException(UserExceptionType.LOGIN);
        }

        if (userRepository.existsByPhone(newUser.getPhone())) {
            log.error("User with phone {} already exists", newUser.getPhone());
            throw new UserAlreadyExistException(UserExceptionType.PHONE);
        }

        if (userRepository.existsByTelegram(newUser.getTelegram())) {
            log.error("User with telegram {} already exists", newUser.getTelegram());
            throw new UserAlreadyExistException(UserExceptionType.TELEGRAM);
        }

        if (userRepository.existsByEmail(newUser.getEmail())) {
            log.error("User with email {} already exists", newUser.getEmail());
            throw new UserAlreadyExistException(UserExceptionType.EMAIL);
        }

        log.debug("User password before encoding: {}", newUser.getPassword());
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        log.debug("User password after encoding: {}", newUser.getPassword());

        log.debug("Before mapper: {}", newUser);
        User user = userMapper.toEntity(newUser);

        log.debug("After mapper: {}", user);
        if (user.getRole() == null) {
            log.debug("Oops!!! Role is null after mapper");
            throw new IllegalArgumentException("Mapper get null role");
        }

        userRepository.save(user);
        log.info("New user {} registered", user);
    }


    public User login(LoginRequest inUser) {

        var userRep = userRepository.findByLogin(inUser.name());

        log.info("Try to login with login {}", inUser.name());

        if (userRep.isEmpty()) {
            throw new UsernameNotFoundException("No exists login " + inUser.name());
        }

        User user = userRep.get();

        log.info("User {} try to login", user.getLogin());
        log.debug("User password: {}", user.getPasswd());
        log.debug("User password in request: {}", inUser.password());
        if (!passwordEncoder.matches(inUser.password(), user.getPasswd())) {
            throw new BadCredentialsException("Wrong password for login " + inUser.name());
        }

        log.info("User {} login OK!", inUser.name());
        return user;
    }

    
}
