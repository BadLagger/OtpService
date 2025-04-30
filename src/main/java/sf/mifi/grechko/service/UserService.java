package sf.mifi.grechko.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sf.mifi.grechko.dto.UserDto;
import sf.mifi.grechko.dto.UserRole;
import sf.mifi.grechko.entity.User;
import sf.mifi.grechko.mapper.UserMapper;
import sf.mifi.grechko.repository.UserRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class UserService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    private final UserMapper userMapper;

    public UserService(UserMapper mapper) {
        userMapper = mapper;
    }

    public void registerNewUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);

        // Проверка на наличие админа в БД (по условию, только один админ может быть зареган)
        if (user.getRole().equals(UserRole.ADMIN) && userRepository.existsByRole(UserRole.ADMIN)) {
            throw new IllegalArgumentException("User with ADMIN role already exists");
        }

        // Хэшируем пароль
        user.setPasswd(passwordService.encodePassword(user.getPasswd()));

        // Сохраняем и пытаемся поймать эксепшн, который скорей всего произойдёт в том случае если данные пользователя не уникальны
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    public UserRole authenticate(String username, String password) {
        User user = userRepository.findByLogin(username);

        if (user == null)
            throw new UsernameNotFoundException("User not found");

        if (!passwordService.matchesPassword(password, user.getPasswd())) {
            throw new BadCredentialsException("Invalid password");
        }
        return user.getRole();
    }
}
