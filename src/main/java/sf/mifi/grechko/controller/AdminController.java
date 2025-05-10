package sf.mifi.grechko.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sf.mifi.grechko.dto.UserRole;
import sf.mifi.grechko.entity.User;
import sf.mifi.grechko.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public List<User> getAllUsers(){
        return userService.getAllUsers(UserRole.USER);
    }

    @PutMapping("/del/{user_id}")
    public ResponseEntity<?> delUserById(@PathVariable("user_id") Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok("User deleted successful");
    }
}
