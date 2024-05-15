package usts.paperms.paperms.controller;

import cn.hutool.json.JSONObject;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usts.paperms.paperms.common.Result;
import usts.paperms.paperms.entity.User;
import usts.paperms.paperms.entity.Users;
import usts.paperms.paperms.service.LogSaveService;
import usts.paperms.paperms.service.SecurityService.JsonConverter;
import usts.paperms.paperms.service.UserService;
import usts.paperms.paperms.service.EmailService;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailController{

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private LogSaveService logSaveService;

    @GetMapping("/register")
    public Result sendCode(String to) throws MessagingException {
        //首先检验邮箱是否已经注册
        if(userService.findEmail(to)) {
            String randomCode = emailService.email(to);
            //将randomCode存储在redis中，设置过期时间为5分钟
            redisTemplate.opsForValue().set(to, randomCode, Duration.ofSeconds(300));
            //返回200状态码
            return Result.success("验证码已发送");
        }else {
            //返回500状态码
            return Result.fail("发送失败");
        }
    }

    @GetMapping("/checkCode")
    public String checkCode(String code, String to) {
        //从redis中获取验证码
        String randomCode = redisTemplate.opsForValue().get(to);
        if (randomCode == null) {
            return "验证码未发送";
        }
        if (code.equals(randomCode)) {
            return "验证码正确";
        }
        return "验证码错误";
    }

    @PostMapping("/change_email")
    public String changeEmail(@RequestParam("email") String email,
                              @RequestParam("username")String username) {
        if(userService.findEmail(email)) {
            return "邮箱已存在";
        }
        if(userService.changeEmail(username, email)) {
            return "修改成功";
        }
        return "修改失败";
    }

    @PostMapping("/email_login")
    public Result emailLogin(@RequestParam("email") String email,
                             @RequestParam("code") String code) {
        String randomCode = redisTemplate.opsForValue().get(email);
        if (randomCode == null) {
            return Result.fail("验证码未发送");
        }
        if (code.equals(randomCode)) {
            Users user = userService.findUserByEmail(email);
            Map<String, Object> data = JsonConverter.createMap();
            data.put("role", userService.findRoleByUsername(user.getUsername()).get());
            data.put("username", user.getUsername());
            data.put("realName", user.getRealName());
            data.put("college", user.getCollege());
            data.put("tel", user.getTel());
            data.put("email", user.getEmail());
            String json = JsonConverter.convertToJson(data);
            JSONObject jsonObject = new JSONObject(json);
            logSaveService.saveLog("用户进行登录", user.getUsername());
            return Result.ok("Login successful").body(jsonObject);
        }
        return Result.fail("验证码错误");
    }
}
