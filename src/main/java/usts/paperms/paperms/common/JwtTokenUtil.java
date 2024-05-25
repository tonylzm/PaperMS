package usts.paperms.paperms.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        return getClaimsFromToken(token) != null;
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }
    public long getExpirationInSeconds() {
        return expiration;
    }
    //检验token是否有效
    public boolean isValidToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return false;
        }
        token = token.substring(7);  // Remove "Bearer " prefix
        if (!validateToken(token)) {
            return false;
        }
        String username = getUsernameFromToken(token);

        return redisTemplate.hasKey(username) && token.equals(redisTemplate.opsForValue().get(username));
    }

}

