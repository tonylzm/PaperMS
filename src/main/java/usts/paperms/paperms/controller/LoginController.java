package usts.paperms.paperms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usts.paperms.paperms.Repository.UserRepository;
import usts.paperms.paperms.entity.LoginRequest;
import usts.paperms.paperms.entity.User;
import usts.paperms.paperms.security.PasswordEncryptionService;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncryptionService passwordEncryption;
  
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());

        if (user == null || !user.getPassword().equals(passwordEncryption.encryptPassword(loginRequest.getPassword(),user.getSalt()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        // 登录成功，返回角色权限信息和登录成功信息
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("role", user.getRole());
        // 可以根据需要添加其他信息
        return ResponseEntity.ok(response);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // 检查用户名是否已经存在
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        // 生成随机盐
        String salt = passwordEncryption.generateSalt();
        user.setSalt(salt);
        user.setPassword(passwordEncryption.encryptPassword(user.getPassword(),salt));

        //校验密码是否符合要求
        userRepository.save(user);
        return ResponseEntity.ok("Registration successful");
    }



    }



