package usts.paperms.paperms.service.SecurityService;

import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
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
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class DecryptFileService {
    @Value("${publicKey}")
    private String publicKeyStr;

    @Value("${privateKey}")
    private String privateKeyStr;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @PostConstruct
    public void init() throws Exception {
        this.publicKey = loadPublicKey(publicKeyStr);
        this.privateKey = loadPrivateKey(privateKeyStr);
    }

    private PublicKey loadPublicKey(String pem) throws Exception {
        String publicKeyPEM = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private PrivateKey loadPrivateKey(String pem) throws Exception {
        String privateKeyPEM = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

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
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] encryptedAesKey = Base64.getDecoder().decode(encryptedAesKeyBase64);
        byte[] decryptedAesKeyBytes = cipher.doFinal(encryptedAesKey);

        // 将解密后的字节数组转换为字符串
        return new String(decryptedAesKeyBytes, "UTF-8");
    }
    public String calculateMD5(byte[] bytes) {
        return DigestUtils.md5Hex(bytes);
    }
}
