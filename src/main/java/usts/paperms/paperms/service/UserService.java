package usts.paperms.paperms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usts.paperms.paperms.Repository.KeyRepository;
import usts.paperms.paperms.Repository.RoleRepository;
import usts.paperms.paperms.Repository.SaltRepository;
import usts.paperms.paperms.Repository.testUserRepository;
import usts.paperms.paperms.entity.*;
import usts.paperms.paperms.security.PasswordEncryptionService;

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
    @Autowired
    private PasswordEncryptionService passwordEncryption;

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
    //用户修改密码
    public String updatePasswordByUsername(String username,String old,String password) {
        Users user = userRepository.findByUsername(username);
        //查找用户的盐
        Optional<Salt> saltOptional = saltRepository.findByUsers(user);
        //输出盐
        if(saltOptional.isEmpty()){
            return "用户不存在";
        }
        Salt salt = saltOptional.get();
        System.out.println(salt.getValue());
        //将旧密码加盐
        old=passwordEncryption.encryptPassword(old,salt.getValue());
        //检测旧密码是否正确
        if (!user.getPassword().equals(old)) {
            return "旧密码错误";
        }
        // 生成随机盐
        String salt1 = passwordEncryption.generateSalt();
        // 生成加密后的密码
        password = passwordEncryption.encryptPassword(password, salt1);
        //更新盐
        saltOptional.get().setValue(salt1);
        saltRepository.save(saltOptional.get());
        user.setPassword(password);
        userRepository.save(user);
        return "修改成功";
    }

    //用户修改密码2
    public String updatePassword(String username,String password) {
        Users user = userRepository.findByUsername(username);
        Optional<Salt> saltOptional = saltRepository.findByUsers(user);
        //输出盐
        if(saltOptional.isEmpty()){
            return "用户不存在";
        }
        // 生成随机盐
        String salt1 = passwordEncryption.generateSalt();
        // 生成加密后的密码
        password = passwordEncryption.encryptPassword(password, salt1);
        //更新盐
        saltOptional.get().setValue(salt1);
        saltRepository.save(saltOptional.get());
        user.setPassword(password);
        userRepository.save(user);
        return "修改成功";
    }

    //用户修改邮箱
    public Boolean changeEmail(String username, String email) {
        Users user = userRepository.findByUsername(username);
        if(user == null){
            return false;
        }
        user.setEmail(email);
        userRepository.save(user);
        return true;
    }

    public Users createUser(Users users) {
        return userRepository.save(users);
    }

    public Boolean findEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

    //通过邮箱查找用户
    public Users findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Users findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void deleteUser(Users users) {
        //首先删除关联表
        Optional<UserRole> userRoleOptional = roleRepository.findByUsers(users);
        if (userRoleOptional.isPresent()) {
            roleRepository.delete(userRoleOptional.get());
        }
        Optional<Salt> saltOptional = saltRepository.findByUsers(users);
        if (saltOptional.isPresent()) {
            saltRepository.delete(saltOptional.get());
        }
        Optional<Key> keyOptional = keyRepository.findByUsers(users);
        if (keyOptional.isPresent()) {
            keyRepository.delete(keyOptional.get());
        }
        //再删除用户表
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
    public Page<Users> findPageByClassCheck(Integer pageNum, Integer pageSize, String role, String college, String name) {
        // 构建分页请求对象
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        // 调用 Spring Data JPA 的方法执行分页查询
        return userRepository.findUsersByUserRole(role, college,name,pageable);
    }
    //更新用户角色
    public String updateRoleByUsername(String username, String role) {
        Users user = userRepository.findByUsername(username);
        Optional<UserRole> userRoleOptional = roleRepository.findByUsers(user);
        if (userRoleOptional.isPresent()) {
            UserRole userRole = userRoleOptional.get();
            userRole.setRole(role);
            roleRepository.save(userRole);
            return "修改成功";
        }else {
            return "用户不存在";
        }
    }

    //查找对应权限的用户real_name
    public List<Users> findUsersByUser(String role, String college) {
        return userRepository.findUsersByUser(role, college);
    }


    // 其他操作方法
}
