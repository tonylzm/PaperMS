package usts.paperms.paperms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import usts.paperms.paperms.Repository.KeyRepository;
import usts.paperms.paperms.Repository.RoleRepository;
import usts.paperms.paperms.Repository.SaltRepository;
import usts.paperms.paperms.Repository.testUserRepository;
import usts.paperms.paperms.entity.*;

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
    @Autowired
    private KeyRepository keyRepository;

    public Users createUserWithRoleAndSalt(Users users, String role, String saltValue,String publicKey,String privateKey) {
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

        // 创建密钥对象
        Key key = new Key();
        key.setUsers(savedUser);
        key.setKeyName(users.getRealName());
        key.setKeyPublic(publicKey);
        key.setKeyPrivate(privateKey);

        // 保存密钥对象
        keyRepository.save(key);

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

    //通过用户名查公钥
    public Optional<String> findPublicKeyByUsername(String username) {
        Optional<Users> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            Optional<Key> keyOptional = keyRepository.findByUsers(user);
            if (keyOptional.isPresent()) {
                Key key = keyOptional.get();
                return Optional.of(key.getKeyPublic());
            }
        }
        return Optional.empty();
    }

    public Optional<String> findPrivateKeyByUsername(String username) {
        Optional<Users> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            Optional<Key> keyOptional = keyRepository.findByUsers(user);
            if (keyOptional.isPresent()) {
                Key key = keyOptional.get();
                return Optional.of(key.getKeyPrivate());
            }
        }
        return Optional.empty();
    }

    //分页输出所有用户
    public List<Users> findAllUser() {
        return userRepository.findAll();
    }

    public Page<Users> findByUsernameContaining(String username, Pageable pageable) {
        return userRepository.findByUsernameContaining(username, pageable);
    }

    //分页查找classCheck通过的文件，两个表关联查询
    public Page<Users> findPageByClassCheck(Integer pageNum, Integer pageSize, String role, String college) {
        // 构建分页请求对象
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        // 调用 Spring Data JPA 的方法执行分页查询
        return userRepository.findUsersByUserRole(role, college,pageable);
    }

    // 其他操作方法
}
