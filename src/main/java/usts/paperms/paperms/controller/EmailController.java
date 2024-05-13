package usts.paperms.paperms.controller;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usts.paperms.paperms.service.UserService;
import usts.paperms.paperms.service.EmailService;

import java.time.Duration;

@RestController
@RequestMapping("/api/email")
public class EmailController{

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/register")
    public String sendCode(String to) throws MessagingException {
        //首先检验邮箱是否已经注册
        if(userService.findEmail(to)) {
            String randomCode = emailService.email(to);
            //将randomCode存储在redis中，设置过期时间为5分钟
            redisTemplate.opsForValue().set(to, randomCode, Duration.ofSeconds(300));
            return "验证码已发送到指定邮箱";
        }else {
            return "邮箱不存在";
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
    public String changeEmail(String email, String username) {
        if(userService.findEmail(email)) {
            return "邮箱已存在";
        }
        if(userService.changeEmail(username, email)) {
            return "修改成功";
        }
        return "修改失败";
    }
}
