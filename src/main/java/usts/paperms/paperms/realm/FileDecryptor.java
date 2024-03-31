package usts.paperms.paperms.realm;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class FileDecryptor {

    public static void decryptFile(String encryptedFilePath, String decryptedFilePath, String keyString) throws Exception {
        byte[] encryptedData = Files.readAllBytes(Paths.get(encryptedFilePath));
        byte[] keyBytes = Base64.getDecoder().decode(keyString);

        // 构建密钥对象
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        // 构建解密器
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, new byte[12]); // 这里假设初始向量(iv)是12字节的零
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        // 解密文件并保存
        byte[] decryptedData = cipher.doFinal(encryptedData);
        try (FileOutputStream fos = new FileOutputStream(decryptedFilePath)) {
            fos.write(decryptedData);
        }
    }

    public static void main(String[] args) {
        String encryptedFilePath = "src/main/java/usts/paperms/paperms/realm/encrypted_file.pdf";
        String decryptedFilePath = "src/main/java/usts/paperms/paperms/realm/decrypted_file.pdf";
        String keyString = "/RlK56BBpmngmQRFuBkH9GZcM7oM0LmRPtZXMGb2R2c=";

        try {
            decryptFile(encryptedFilePath, decryptedFilePath, keyString);
            System.out.println("文件解密成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
