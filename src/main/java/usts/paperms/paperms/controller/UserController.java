/**
 * 用户控制器类
 * 主要包含用户注册、登录等功能
 * 包含用户IP检查功能，包含生成RSA密钥对功能
 */
package usts.paperms.paperms.controller;

import cn.hutool.json.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import usts.paperms.paperms.common.Result;
import usts.paperms.paperms.entity.LoginRequest;
import usts.paperms.paperms.entity.User;
import usts.paperms.paperms.entity.UserRole;
import usts.paperms.paperms.entity.Users;
import usts.paperms.paperms.security.PasswordEncryptionService;
import usts.paperms.paperms.service.LogSaveService;
import usts.paperms.paperms.service.SecurityService.AESKeyGenerationService;
import usts.paperms.paperms.service.SecurityService.JsonConverter;
import usts.paperms.paperms.service.SecurityService.RSAKeyGenerationService;
import usts.paperms.paperms.service.UserService;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncryptionService passwordEncryption;
    @Autowired
    private RSAKeyGenerationService rsaKeyGenerationService;
    @Autowired
    private AESKeyGenerationService aesKeyGenerationService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private LogSaveService logSaveService;

    private final String BAIDU_MAP_API_KEY = "RY1MJlgS9FiUEQFzZPdtCRpe538IduzF";
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
        user.setUserRole(role);
        // 保存用户信息及角色信息
        Users savedUser = userService.createUser(user);
        return ResponseEntity.ok(savedUser);
    }

    //出卷人注册方法
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam("actor")String actor,
                                      @RequestBody Users request) throws Exception {
        return ResponseEntity.ok(userService.register(request,"user",actor));
    }

    //审批人(系)注册方法
    @PostMapping("/check_register")
    public ResponseEntity<?> check_register(@RequestParam("actor")String actor,
                                            @RequestBody Users request) throws Exception {
        return ResponseEntity.ok(userService.register(request,"check",actor));
    }
    //院长注册方法
    @PostMapping("/college_register")
    public ResponseEntity<?> college_register(@RequestParam("actor")String actor,
                                              @RequestBody Users request) throws Exception {
        return ResponseEntity.ok(userService.register(request,"college",actor));
    }

    //角色权限变化方法
    @PostMapping("/role")
    public ResponseEntity<?> changeRole(@RequestParam("Actor")String Actor,
                                        @RequestParam("username") String username,
                                        @RequestParam("role") String role) {
        return ResponseEntity.ok(userService.updateRoleByUsername(Actor,username, role));
    }

    //查找相应权限的用户
    @PostMapping("/userrole")
    public Result findCheckedfile(@RequestParam Integer pageNum,
                                  @RequestParam Integer pageSize,
                                  @RequestParam("college")String college,
                                  @RequestParam("role")String role,
                                  @RequestParam(defaultValue = "") String name) {
        Page<Users> page=userService.findPageByClassCheck(pageNum,pageSize,role,college,name);
        return Result.success(page);
    }
    //解析IP地址，并且判断用户是否存在异常访问
    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody Map<String, Object> data) throws Exception {
        //将username，time,ip信息存储到redis中
        String username = (String) data.get("username");
        String time = (String) data.get("time");
        String ipAddress = (String) data.get("ipAddress");
        String hashedPassword = (String) data.get("hashedPassword");


        String url = "http://api.map.baidu.com/location/ip?ak=" + BAIDU_MAP_API_KEY + "&ip=" + ipAddress;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        // 解析响应并输出地址信息
        // 这里需要根据百度地图API的返回结果进行解析，具体实现需要根据返回的 JSON 结果进行处理
        // 省略了解析过程，你需要根据实际情况解析响应并提取地址信息
        // 解析JSON响应
        JSONObject jsonResponse = new JSONObject(response);
        JSONObject content = jsonResponse.getJSONObject("content");
        JSONObject addressDetail = content.getJSONObject("address_detail");

        // 提取国家、省份和城市信息
        String country = "中国"; // 默认为中国，因为百度地图API返回的是国内IP地址
        String province = addressDetail.getStr("province");
        String city = addressDetail.getStr("city");

        //如果country不在江苏省，返回错误信息
        if(!province.equals("江苏省") && !city.equals("苏州市")){
            return ResponseEntity.badRequest().body("非法访问");
        }

        redisTemplate.opsForValue().set("username:" + username, username, Duration.ofSeconds(120));
        redisTemplate.opsForValue().set("time:" + username, time, Duration.ofSeconds(120));
        redisTemplate.opsForValue().set("ipAddress:" + username, ipAddress, Duration.ofSeconds(120));
        redisTemplate.opsForValue().set("hashedPassword:" + username, hashedPassword, Duration.ofSeconds(120));
        return ResponseEntity.ok("认证信息生成完成");
    }
    @GetMapping("/getPublicKey/{username}")
    public ResponseEntity<?> getPublicKey(@PathVariable String username) {
        // 从 Redis 中读取公钥
        String ipAddress= redisTemplate.opsForValue().get("ipAddress:" + username);
//        // 解析IP地址的地理位置
//        String country = "Unknown";
//        String city = "Unknown";
//
//        if (ipAddress != null) {
//            try {
//                File database = new File("src/main/resources/static/files/city/GeoLite2-City.mmdb");
//                DatabaseReader reader = new DatabaseReader.Builder(database).build();
//                InetAddress ip = InetAddress.getByName(ipAddress);
//                CityResponse response = reader.city(ip);
//
//                country = response.getCountry().getName();
//                city = response.getCity().getName();
//            } catch (IOException | GeoIp2Exception e) {
//                e.printStackTrace();
//            }
//        }
        String url = "http://api.map.baidu.com/location/ip?ak=" + BAIDU_MAP_API_KEY + "&ip=" + ipAddress;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        // 解析响应并输出地址信息
        // 解析JSON响应
        JSONObject jsonResponse = new JSONObject(response);
        JSONObject content = jsonResponse.getJSONObject("content");
        JSONObject addressDetail = content.getJSONObject("address_detail");
        // 提取国家、省份和城市信息
        String country = "中国";
        String province = addressDetail.getStr("province");
        String city = addressDetail.getStr("city");
        // 构造明文地址信息
        String plaintextAddress = country + " " + province + " " + city;
        return ResponseEntity.ok(plaintextAddress);
    }

    //输出用户某些基本信息
    @PostMapping("/findsalt")
    public ResponseEntity<?> findSaltByUsername(@RequestBody Users request) {
        return ResponseEntity.ok(userService.findSaltByUsername(request.getUsername()));
    }

    @PostMapping("/findrole")
    public ResponseEntity<?> findRoleByUsername(@RequestBody Users request) {
        return ResponseEntity.ok(userService.findUserByUsername(request.getUsername()));
    }

    @PostMapping("/find_public_key")
    public ResponseEntity<?> findKeyByUsername(@RequestBody Users request) {
        return ResponseEntity.ok(userService.findPublicKeyByUsername(request.getUsername()));
    }
    @PostMapping("/find_private_key")
    public ResponseEntity<?> findPrivateKeyByUsername(@RequestBody Users request) {
        return ResponseEntity.ok(userService.findPrivateKeyByUsername(request.getUsername()));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam("actor")String actor,
                                        @RequestBody Users request) {
        Users user = userService.findUserByUsername(request.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        userService.deleteUser(user);
        logSaveService.saveLog("用户"+user.getUsername()+"删除",actor);
        return ResponseEntity.ok("User deleted successfully");
    }

    //登录方法实现
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
        data.put("realName", user.getRealName());
        data.put("college", user.getCollege());
        data.put("tel", user.getTel());
        data.put("email", user.getEmail());
        String json = JsonConverter.convertToJson(data);
        JSONObject jsonObject = new JSONObject(json);
        logSaveService.saveLog("用户进行登录",user.getRealName());
        return Result.ok("Login successful").body(jsonObject);
    }

    @Setter
    @Getter
    public static class CreateUserWithRoleRequest {
        private String username;
        private String password;
        // 省略getter和setter方法
    }
    //密钥生成方法
    @GetMapping("/generate")
    public String generateKeys() {
        try {
            // 调用Service层方法生成公钥和私钥
            rsaKeyGenerationService.generateKeys();
            return "RSA keys generated successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to generate RSA keys.";
        }
    }
    //修改密码
    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, Object> data) {
        String username = (String) data.get("username");
        String oldPassword = (String) data.get("oldPassword");
        String newPassword = (String) data.get("newPassword");
        return ResponseEntity.ok(userService.updatePasswordByUsername(username, oldPassword, newPassword));
    }

    //修改密码2
    @PostMapping("/updatePassword2")
    public ResponseEntity<?> updatePassword2(@RequestBody Map<String, Object> data) {
        String username = (String) data.get("username");
        String newPassword = (String) data.get("newPassword");
        return ResponseEntity.ok(userService.updatePassword(username, newPassword));
    }

    //查找角色为审批人的用户
    @PostMapping("/findCheckUser")
    public Result findCheckUser(@RequestParam("college")String college
                               ) {
        List<Users> users_class = userService.findUsersByUser("check", college);
        List<Users> users_college = userService.findUsersByUser("college", college);
        List<String> realNames = users_class.stream()
                .map(Users::getRealName)
                .toList();
        List<String> realName = users_college.stream()
                .map(Users::getRealName)
                .toList();
        Map<String, List<String>> result = new HashMap<>();
        result.put("class_check", realNames);
        result.put("college_check", realName);
        return Result.success(result);
    }

    @GetMapping("/get_teacher")
    public Result getTeacher(){
        return Result.success(userService.findRealNameByUsername());
    }

}

