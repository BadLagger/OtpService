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
            throw new IllegalArgumentException("Username already exists");
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        log.debug("Before mapper: {}, {}, {}", newUser.getName(), newUser.getRole(), newUser.getTelegram());
        User user = userMapper.toEntity(newUser);

        if (user.getRole() == null) {
            log.debug("Oops!!! Role is null after mapper");
            throw new IllegalArgumentException("Mapper get null role");
        }

        userRepository.save(user);
    }


    public User login(LoginRequest inUser) {

        var user = userRepository.findByLogin(inUser.name());

        if (user == null) {
            throw new UsernameNotFoundException(null);
        }

        if (!passwordEncoder.matches(inUser.password(), user.getPasswd())) {
            throw new BadCredentialsException(null);
        }

        return user;
    }
}
