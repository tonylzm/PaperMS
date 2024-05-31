package usts.paperms.paperms.service.SecurityService;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import usts.paperms.paperms.common.MinIoUtil;
import usts.paperms.paperms.config.MinIoProperties;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

@Service
public class RSAFileEncryptionService {
    @Autowired
    private RSAKeyGenerationService rsaKeyGenerationService;
    @Autowired
    MinIoProperties minIoProperties;

    //private static final String PRIVATE_KEY_FILE_PATH = "BOOT-INF/classes/static/files/security/private.der";
    @Value("${service.privatekey-dir}")
    private String PRIVATE_KEY_FILE_PATH;
    //private static final String PUBLIC_KEY_FILE_PATH = "BOOT-INF/classes/resources/static/files/security/public.der";
    @Value("${service.publickey-dir}")
    private String PUBLIC_KEY_FILE_PATH;
    //private static final String ENCRYPTED_FILE_DIRECTORY = "src/main/resources/static/files/";
    @Value("${spring.servlet.multipart.location}")
    private String ENCRYPTED_FILE_DIRECTORY;
    //private static final String AES_KEY_FILE_PATH = "src/main/resources/static/files/AESkey/";
    @Value("${key.upload-dir}")
    private String AES_KEY_FILE_PATH;


    @Value("${publicKey}")
    private String publicKeyStr;
    @Value("${privateKey}")
    private String privateKeyStr;


    private PrivateKey privateKey;
    private PublicKey publicKey;
    @Autowired
    private MinIoUtil minIoUtil;
    @Autowired
    private usts.paperms.paperms.service.pigeonholeService pigeonholeService;

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


    // 加密文件（文件为MultipartFile类型）
    public String encryptFile(MultipartFile inputFile,String filename) throws Exception {
        // 生成AES密钥
        SecretKey aesKey = generateAESKey();
        // 将AES密钥加密
        byte[] encryptedAESKey = encryptAESKey(aesKey);
        try(ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream()) {
            byteArrayOutputStream1.write(encryptedAESKey);
            InputStream file = new ByteArrayInputStream(byteArrayOutputStream1.toByteArray());
            MinIoUtil.upload("keys", filename + ".key", file);
        }
        // 加密文件内容
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
        try (InputStream fileInputStream = inputFile.getInputStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] inputBytes = fileInputStream.readAllBytes();
            byte[] encryptedBytes = aesCipher.doFinal(inputBytes);
            byteArrayOutputStream.write(encryptedBytes);
            // 上传加密后的文件内容
            InputStream encryptedFileInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            return MinIoUtil.upload(minIoProperties.getBucketName(), filename,encryptedFileInputStream);
        }
    }

    //归档加密
    public String pigeonhole(MultipartFile inputFile,String filename) throws Exception {
        // 生成AES密钥
        SecretKey aesKey = generateAESKey();
        // 将AES密钥加密
        byte[] encryptedAESKey = encryptAESKey(aesKey);
        try(ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream()) {
            byteArrayOutputStream1.write(encryptedAESKey);
            InputStream file = new ByteArrayInputStream(byteArrayOutputStream1.toByteArray());
            MinIoUtil.upload("pigeonholekeys", filename + ".key", file);
        }
        // 加密文件内容
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
        try (InputStream fileInputStream = inputFile.getInputStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] inputBytes = fileInputStream.readAllBytes();
            byte[] encryptedBytes = aesCipher.doFinal(inputBytes);
            byteArrayOutputStream.write(encryptedBytes);
            // 上传加密后的文件内容
            InputStream encryptedFileInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            return MinIoUtil.upload("pigeonhole", filename,encryptedFileInputStream);
        }
    }

    //加密文件（文件为byte[]类型）
    public String encryptFiles(byte[] inputFile,String filename) throws Exception {
        // 生成AES密钥
        SecretKey aesKey = generateAESKey();
        // 将AES密钥加密
        byte[] encryptedAESKey = encryptAESKey(aesKey);
        try(ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream()) {
            byteArrayOutputStream1.write(encryptedAESKey);
            InputStream file = new ByteArrayInputStream(byteArrayOutputStream1.toByteArray());
            MinIoUtil.upload("keys", filename + ".key", file);
        }
        // 加密文件内容
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
        try (InputStream fileInputStream = new ByteArrayInputStream(inputFile);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] inputBytes = fileInputStream.readAllBytes();
            byte[] encryptedBytes = aesCipher.doFinal(inputBytes);
            byteArrayOutputStream.write(encryptedBytes);
            // 上传加密后的文件内容
            InputStream encryptedFileInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            return MinIoUtil.upload(minIoProperties.getBucketName(), filename,encryptedFileInputStream);
        }
    }


    // 解密文件的方法
    public String decryptFiles(String encryptedFileName) throws Exception {
        // 从MinIO下载加密的AES密钥
        InputStream encryptedKeyStream = MinIoUtil.getFileStream("keys", encryptedFileName + ".key");
        byte[] encryptedKeyBytes = encryptedKeyStream.readAllBytes();
        SecretKey aesKey = decryptAESKey(encryptedKeyBytes);

        // 从MinIO下载加密的文件内容
        InputStream encryptedFileStream = MinIoUtil.getFileStream(minIoProperties.getBucketName(),encryptedFileName);
        byte[] encryptedFileBytes = encryptedFileStream.readAllBytes();

        // 解密文件内容
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decryptedBytes = aesCipher.doFinal(encryptedFileBytes);

        // 上传解密后的文件内容到MinIO
        InputStream decryptedFileInputStream = new ByteArrayInputStream(decryptedBytes);
        String decryptedFileUrl = MinIoUtil.upload(minIoProperties.getBucketName(), encryptedFileName, decryptedFileInputStream);

        // 删除存储的AES密钥文件
        MinIoUtil.deleteFile("keys", encryptedFileName + ".key");

        return decryptedFileUrl;
    }

    //归档解密方法
    public String pigeonholeDecrypt(String encryptedFileName) throws Exception {
        if(pigeonholeService.findDecryptedByFileName(encryptedFileName)){
            return "File already decrypted";
        }
        // 从MinIO下载加密的AES密钥
        InputStream encryptedKeyStream = MinIoUtil.getFileStream("pigeonholekeys", encryptedFileName + ".key");
        byte[] encryptedKeyBytes = encryptedKeyStream.readAllBytes();
        SecretKey aesKey = decryptAESKey(encryptedKeyBytes);

        // 从MinIO下载加密的文件内容
        InputStream encryptedFileStream = MinIoUtil.getFileStream("pigeonhole",encryptedFileName);
        byte[] encryptedFileBytes = encryptedFileStream.readAllBytes();

        // 解密文件内容
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decryptedBytes = aesCipher.doFinal(encryptedFileBytes);

        // 上传解密后的文件内容到MinIO
        InputStream decryptedFileInputStream = new ByteArrayInputStream(decryptedBytes);
        String decryptedFileUrl = MinIoUtil.upload("pigeonhole", encryptedFileName, decryptedFileInputStream);

        // 删除存储的AES密钥文件
        MinIoUtil.deleteFile("pigeonholekeys", encryptedFileName + ".key");

        pigeonholeService.updateDecrypted(encryptedFileName, true);

        return decryptedFileUrl;
    }

    // 生成AES密钥
    private SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    public byte[] encryptAESKey(SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(aesKey.getEncoded());
    }

    public SecretKey decryptAESKey(byte[] encryptedAESKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedAESKey = cipher.doFinal(encryptedAESKey);
        return new SecretKeySpec(decryptedAESKey, "AES");
    }

    public File convertByteArrayToFile(byte[] byteArray, String fileName) throws IOException {
        File targetFile = new File(fileName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
            fileOutputStream.write(byteArray);
        }
        return targetFile;
    }
}