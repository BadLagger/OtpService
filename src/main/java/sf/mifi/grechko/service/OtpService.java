package sf.mifi.grechko.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Autowired
    private PasswordService passwordService;

    private final UserMapper userMapper;

    public OtpService(UserMapper userMapper) {
        this.userMapper = userMapper;
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
}
