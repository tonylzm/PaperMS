package usts.paperms.paperms.service;

import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Service
public class AESKeyGenerationService {
    private static final String PRIVATE_KEY_FILE_PATH = "src/main/resources/static/files/security/keyfile.key";
    // 生成AES密钥并存储在指定路径
    public void generateAESKey() throws NoSuchAlgorithmException, IOException {
        // 创建AES密钥生成器
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        // 初始化密钥生成器
        keyGen.init(128); // 使用AES算法，密钥长度为256位

        // 生成AES密钥
        SecretKey secretKey = keyGen.generateKey();

        // 将密钥存储到文件
        storeKeyToFile(secretKey);
    }

    // 将密钥存储到文件
    private void storeKeyToFile(SecretKey secretKey) throws IOException {
        // 将密钥编码为字节数组
        byte[] keyBytes = secretKey.getEncoded();

        try (FileOutputStream fos = new FileOutputStream(AESKeyGenerationService.PRIVATE_KEY_FILE_PATH)) {
            fos.write(keyBytes); // 写入密钥字节数组到文件
        }
    }


}