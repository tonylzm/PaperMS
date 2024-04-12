package usts.paperms.paperms.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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

    //生成AES密钥并且返回AES密钥值
    public String generateAESKeyAndReturn() throws NoSuchAlgorithmException, IOException {
        // 创建AES密钥生成器
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);

        // 生成AES密钥
        SecretKey secretKey = keyGen.generateKey();
        //将密钥转化为Base64字符串
        String key= Base64.getEncoder().encodeToString(secretKey.getEncoded());
        return key;
    }
    // 将密钥存储到文件
    private void storeKeyToFile(SecretKey secretKey) throws IOException {
        // 将密钥编码为字节数组
        byte[] keyBytes = secretKey.getEncoded();

        try (FileOutputStream fos = new FileOutputStream(AESKeyGenerationService.PRIVATE_KEY_FILE_PATH)) {
            fos.write(keyBytes); // 写入密钥字节数组到文件
        }
    }


    //AES密钥解密,传入参数为加密的AES密钥和用于解密的AES密钥
    public String decryptAESKey(String encryptedAESKey, String aesKeyBase64) throws Exception {
        // 将 Base64 编码的 AES 密钥转换为字节数组

        byte[] aesKeyBytes = Base64.getDecoder().decode(aesKeyBase64);
        // 创建 AES 密钥对象
        SecretKeySpec aesKeySpec = new SecretKeySpec(aesKeyBytes, "AES");

        // 创建 AES 解密器
        Cipher cipher = Cipher.getInstance("AES");
        // 使用 AES 密钥初始化解密器
        cipher.init(Cipher.DECRYPT_MODE, aesKeySpec);

        // 将 Base64 编码的密文解码为字节数组
        byte[] encryptedAESKeyBytes = Base64.getDecoder().decode(encryptedAESKey);
        // 执行解密操作
        byte[] decryptedAESKeyBytes = cipher.doFinal(encryptedAESKeyBytes);

        // 将解密后的字节数组转换为字符串并返回
        return new String(decryptedAESKeyBytes);
    }

}