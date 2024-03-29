package usts.paperms.paperms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usts.paperms.paperms.Repository.RoleRepository;
import usts.paperms.paperms.Repository.SaltRepository;
import usts.paperms.paperms.Repository.testUserRepository;
import usts.paperms.paperms.entity.Salt;
import usts.paperms.paperms.entity.UserRole;
import usts.paperms.paperms.entity.Users;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private testUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SaltRepository saltRepository;

    public Users createUserWithRoleAndSalt(Users users, String role, String saltValue) {
        // 创建用户对象
        Users savedUser = userRepository.save(users);

        // 创建角色对象
        UserRole roleObj = new UserRole();
        roleObj.setRole(role);
        roleObj.setUsers(savedUser);

        // 保存角色对象
        roleRepository.save(roleObj);

        // 创建盐对象
        Salt salt = new Salt();
        salt.setUsers(savedUser);
        salt.setValue(saltValue);

        // 保存盐对象
        saltRepository.save(salt);

        return savedUser;
    }

    public Optional<String> findSaltByUsername(String username) {
        Optional<Users> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            Optional<Salt> saltOptional = saltRepository.findByUsers(user);
            if (saltOptional.isPresent()) {
                Salt salt = saltOptional.get();
                return Optional.of(salt.getValue());
            }
        }
        return Optional.empty();
    }
    public Users createUser(Users users) {
        return userRepository.save(users);
    }

    public Users findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(Users users) {
        userRepository.delete(users);
    }

    public Optional<String> findRoleByUsername(String username) {
        Optional<Users> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            Optional<UserRole> userRoleOptional = roleRepository.findByUsers(user);
            if (userRoleOptional.isPresent()) {
                UserRole userRole = userRoleOptional.get();
                return Optional.of(userRole.getRole());
            }
        }
        return Optional.empty();
    }



    // 其他操作方法
}
