package usts.paperms.paperms.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import usts.paperms.paperms.service.impl.InMemoryMultipartFile;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class DecryptFileService {
    private static final String PRIVATE_KEY_FILE_PATH = "src/main/resources/static/files/security/private.der";
    public MultipartFile decryptFile(MultipartFile encryptedFile, String key) throws IOException {
        try {
            byte[] encryptedData = encryptedFile.getBytes();
            byte[] keyBytes = Base64.getDecoder().decode(key);

            // 构建密钥对象
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

            // 构建解密器
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, new byte[12]); // 这里假设初始向量(iv)是12字节的零
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            // 解密文件
            byte[] decryptedData = cipher.doFinal(encryptedData);

            // 将解密后的数据写入 ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(decryptedData);

            // 将 ByteArrayOutputStream 转换为 MultipartFile
            MultipartFile decryptedFile = new InMemoryMultipartFile(encryptedFile.getName(), encryptedFile.getOriginalFilename(),
                    encryptedFile.getContentType(), outputStream.toByteArray());

            return decryptedFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptAesKeyToString(String encryptedAesKeyBase64) throws Exception {
        byte[] privateKeyBytes = Files.readAllBytes(Paths.get(PRIVATE_KEY_FILE_PATH));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] encryptedAesKey = Base64.getDecoder().decode(encryptedAesKeyBase64);
        byte[] decryptedAesKeyBytes = cipher.doFinal(encryptedAesKey);

        // 将解密后的字节数组转换为字符串
        String decryptedAesKeyString = new String(decryptedAesKeyBytes, "UTF-8");

        return decryptedAesKeyString;
    }
    public String calculateMD5(byte[] bytes) {
        return DigestUtils.md5Hex(bytes);
    }
}
