package usts.paperms.paperms.controller;

import cn.hutool.json.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usts.paperms.paperms.common.Result;
import usts.paperms.paperms.entity.LoginRequest;
import usts.paperms.paperms.entity.User;
import usts.paperms.paperms.entity.UserRole;
import usts.paperms.paperms.entity.Users;
import usts.paperms.paperms.security.PasswordEncryptionService;
import usts.paperms.paperms.service.JsonConverter;
import usts.paperms.paperms.service.UserService;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncryptionService passwordEncryption;
    @PostMapping("/create")
    public ResponseEntity<Users> createUserWithRole(@RequestBody CreateUserWithRoleRequest request) {
        // 创建用户对象
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        // 创建角色对象
        UserRole role = new UserRole();
        role.setRole("admin");

        // 将用户和角色关联
        role.setUsers(user);
        user.setRoles(Collections.singletonList(role));

        // 保存用户信息及角色信息
        Users savedUser = userService.createUser(user);

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users request) {
        // 检查用户名是否已经存在
        if (userService.findUserByUsername(request.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        // 生成随机盐
        String salt = passwordEncryption.generateSalt();

        // 创建用户对象
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncryption.encryptPassword(request.getPassword(),salt)); // 此处的密码需要在 Service 层加密
        // 其他用户信息...
        // 创建用户角色并保存用户信息和角色信息
        userService.createUserWithRoleAndSalt(user, "user", salt);

        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/findsalt")
    public ResponseEntity<?> findSaltByUsername(@RequestBody Users request) {
        return ResponseEntity.ok(userService.findSaltByUsername(request.getUsername()));
    }

    @PostMapping("/findrole")
    public ResponseEntity<?> findRoleByUsername(@RequestBody Users request) {
        return ResponseEntity.ok(userService.findUserByUsername(request.getUsername()));
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginRequest loginRequest) {
        Users user = userService.findUserByUsername(loginRequest.getUsername());
        if (user == null || !user.getPassword().equals(passwordEncryption.encryptPassword(loginRequest.getPassword(),userService.findSaltByUsername(loginRequest.getUsername()).get()))) {
            return Result.fail("Invalid username or password");
        }
        // 登录成功，返回角色权限信息和登录成功信息
        Map<String, Object> data = JsonConverter.createMap();
        data.put("role", userService.findRoleByUsername(loginRequest.getUsername()).get());
        data.put("username", loginRequest.getUsername());
        String json = JsonConverter.convertToJson(data);

        JSONObject jsonObject = new JSONObject(json);

        return Result.ok("Login successful").body(jsonObject);
    }

    @Setter
    @Getter
    public static class CreateUserWithRoleRequest {
        private String username;
        private String password;

        // 省略getter和setter方法
    }

}

