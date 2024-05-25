package usts.paperms.paperms.security;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import usts.paperms.paperms.common.JwtTokenUtil;
@Aspect
@Component
public class TokenValidationAspect {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private JwtTokenUtil JwtTokenUtil;

    @Before("@annotation(ValidateToken)")
    public void validateToken() throws IOException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        String token = request.getHeader("Authorization");
        if (!JwtTokenUtil.isValidToken(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token");
        }
    }
}

