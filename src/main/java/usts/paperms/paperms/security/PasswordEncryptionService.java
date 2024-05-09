package usts.paperms.paperms.security;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Service
public class PasswordEncryptionService {

    private static final int SALT_LENGTH = 16; // 盐值长度

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    // 生成盐值
    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // 加密密码
    public String encryptPassword(String password, String salt) {
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        byte[] hashedPassword = hashPassword(password.toCharArray(), saltBytes);
        return Base64.getEncoder().encodeToString(hashedPassword);
    }

    // 验证密码
    public boolean verifyPassword(String providedPassword, String storedPassword, String salt) {
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        byte[] storedHash = Base64.getDecoder().decode(storedPassword);

        byte[] providedHash = hashPassword(providedPassword.toCharArray(), saltBytes);
        int diff = storedHash.length ^ providedHash.length;
        for (int i = 0; i < storedHash.length && i < providedHash.length; i++) {
            diff |= storedHash[i] ^ providedHash[i];
        }
        return diff == 0;
    }

    private byte[] hashPassword(final char[] password, final byte[] salt) {//PBE加密算法
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    // Inner class for response
    public static class VerifyPasswordResponse {
        private boolean isValid;

        public VerifyPasswordResponse(boolean isValid) {
            this.isValid = isValid;
        }

        public boolean isValid() {
            return isValid;
        }

        public void setValid(boolean valid) {
            isValid = valid;
        }
    }
}
