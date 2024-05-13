package usts.paperms.paperms.service;

import cn.hutool.core.util.RandomUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private Integer code;
    @Value("${spring.mail.username}")
    private String from="";
    @Autowired
    private JavaMailSender javaMailSender;
    public String email(String to) throws MessagingException {
        code = RandomUtil.randomInt(100000, 999999);
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setFrom(from);
//        simpleMailMessage.setTo(to);
//        simpleMailMessage.setSubject("test");
//        simpleMailMessage.setText("Code:" + code.toString());
//        javaMailSender.send(simpleMailMessage);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // 第二个参数为true表示发送HTML邮件
        String htmlContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>邮箱验证码</title>\n" +
                "    <style>\n" +
                "        table {\n" +
                "            width: 700px;\n" +
                "            margin: 0 auto;\n" +
                "        }\n" +
                "        #top {\n" +
                "            width: 700px;\n" +
                "            border-bottom: 1px solid #ccc;\n" +
                "            margin: 0 auto 30px;\n" +
                "        }\n" +
                "        #top table {\n" +
                "            font: 12px Tahoma, Arial, 宋体;\n" +
                "            height: 40px;\n" +
                "        }\n" +
                "        #content {\n" +
                "            width: 680px;\n" +
                "            padding: 0 10px;\n" +
                "            margin: 0 auto;\n" +
                "        }\n" +
                "        #content_top {\n" +
                "            line-height: 1.5;\n" +
                "            font-size: 14px;\n" +
                "            margin-bottom: 25px;\n" +
                "            color: #4d4d4d;\n" +
                "        }\n" +
                "        #content_top strong {\n" +
                "            display: block;\n" +
                "            margin-bottom: 15px;\n" +
                "        }\n" +
                "        #content_top strong span {\n" +
                "            color: #f60;\n" +
                "            font-size: 16px;\n" +
                "        }\n" +
                "        #verificationCode {\n" +
                "            color: #f60;\n" +
                "            font-size: 24px;\n" +
                "        }\n" +
                "        #content_bottom {\n" +
                "            margin-bottom: 30px;\n" +
                "        }\n" +
                "        #content_bottom small {\n" +
                "            display: block;\n" +
                "            margin-bottom: 20px;\n" +
                "            font-size: 12px;\n" +
                "            color: #747474;\n" +
                "        }\n" +
                "        #bottom {\n" +
                "            width: 700px;\n" +
                "            margin: 0 auto;\n" +
                "        }\n" +
                "        #bottom div {\n" +
                "            padding: 10px 10px 0;\n" +
                "            border-top: 1px solid #ccc;\n" +
                "            color: #747474;\n" +
                "            margin-bottom: 20px;\n" +
                "            line-height: 1.3em;\n" +
                "            font-size: 12px;\n" +
                "        }\n" +
                "        #content_top strong span {\n" +
                "            font-size: 18px;\n" +
                "            color: #FE4F70;\n" +
                "        }\n" +
                "        #sign {\n" +
                "            text-align: right;\n" +
                "            font-size: 18px;\n" +
                "            color: #FE4F70;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "        #verificationCode {\n" +
                "            height: 100px;\n" +
                "            width: 680px;\n" +
                "            text-align: center;\n" +
                "            margin: 30px 0;\n" +
                "        }\n" +
                "        #verificationCode div {\n" +
                "            height: 100px;\n" +
                "            width: 680px;\n" +
                "        }\n" +
                "        .button {\n" +
                "            color: #FE4F70;\n" +
                "            margin-left: 10px;\n" +
                "            height: 80px;\n" +
                "            width: auto;\n" +
                "            resize: none;\n" +
                "            font-size: 42px;\n" +
                "            border: none;\n" +
                "            outline: none;\n" +
                "            padding: 10px 15px;\n" +
                "            background: #ededed;\n" +
                "            text-align: center;\n" +
                "            border-radius: 17px;\n" +
                "            box-shadow: 6px 6px 12px #cccccc,\n" +
                "            -6px -6px 12px #ffffff;\n" +
                "        }\n" +
                "        .button:hover {\n" +
                "            box-shadow: inset 6px 6px 4px #d1d1d1,\n" +
                "            inset -6px -6px 4px #ffffff;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<table>\n" +
                "    <tbody>\n" +
                "    <tr>\n" +
                "        <td>\n" +
                "            <div id=\"top\">\n" +
                "                <table>\n" +
                "                    <tbody><tr><td></td></tr></tbody>\n" +
                "                </table>\n" +
                "            </div>\n" +
                "            <div id=\"content\">\n" +
                "                <div id=\"content_top\">\n" +
                "                    <strong>尊敬的用户：您好！</strong>\n" +
                "                    <strong>\n" +
                "                        您正在进行<span>信息修改</span>操作，请在验证码中输入以下验证码完成操作：\n" +
                "                    </strong>\n" +
                "                    <div id=\"verificationCode\">\n" +
                "                        <button class=\"button\" th:each=\"a:${verifyCode}\">" + code + "</button>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div id=\"content_bottom\">\n" +
                "                    <small>\n" +
                "                        注意：此操作可能会修改您的密码、登录邮箱或绑定手机。如非本人操作，请及时登录并修改密码以保证帐户安全\n" +
                "                        <br>（工作人员不会向你索取此验证码，请勿泄漏！)\n" +
                "                    </small>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <div id=\"bottom\">\n" +
                "                <div>\n" +
                "                    <p>此为系统邮件，请勿回复<br>\n" +
                "                        请保管好您的邮箱，避免账号被他人盗用\n" +
                "                    </p>\n" +
                "                    <p id=\"sign\">——USTS_苏州科技大学</p>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    </tbody>\n" +
                "</table>\n" +
                "</body>"; // 这里是你的HTML内容
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("验证码邮件");
        helper.setText(htmlContent, true); // 第二个参数为true表示是HTML内容
        javaMailSender.send(mimeMessage);
        return code.toString();
    }
}
