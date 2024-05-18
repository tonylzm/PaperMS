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

    //    public RSAFileEncryptionService() {
//        // Constructor remains empty
//        //将字符串转换为公钥和私钥
//        try {
//            byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr);
//            byte[] privateBytes = Base64.getDecoder().decode(privateKeyStr);
//            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicBytes);
//            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateBytes);
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            publicKey = keyFactory.generatePublic(publicSpec);
//            privateKey = keyFactory.generatePrivate(privateSpec);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
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

//    private PrivateKey loadPrivateKey(String filePath) throws Exception {
//        FileInputStream fis = new FileInputStream(filePath);
//        byte[] keyBytes = new byte[fis.available()];
//        fis.read(keyBytes);
//        fis.close();
//        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        return keyFactory.generatePrivate(spec);
//    }
//
//    private PublicKey loadPublicKey(String filePath) throws Exception {
//        FileInputStream fis = new FileInputStream(filePath);
//        byte[] keyBytes = new byte[fis.available()];
//        fis.read(keyBytes);
//        fis.close();
//        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        return keyFactory.generatePublic(spec);
//    }

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
//        File encryptedFile = new File(filename);
//        try (InputStream fileInputStream = new ByteArrayInputStream(inputFile);
//             OutputStream fileOutputStream = new FileOutputStream(encryptedFile)) {
//            byte[] inputBytes = fileInputStream.readAllBytes();
//            byte[] encryptedBytes = aesCipher.doFinal(inputBytes);
//            fileOutputStream.write(encryptedBytes);
//        }
//        return encryptedFile;
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

//    public void decryptFile(File encryptedFile) throws Exception {
//        // 从文件中读取加密的AES密钥
//        File fileInputStreams = MinIoUtil.getFile("keys",encryptedFile.getName()+".key");
//        byte [] fileContent = Files.readAllBytes(fileInputStreams.toPath());
//        //minio读取加密的AES密钥
//        // 使用RSA私钥解密AES密钥
//        SecretKey aesKey = decryptAESKey(fileContent);
//        // 解密文件内容并替换原加密文件
//        Cipher aesCipher = Cipher.getInstance("AES");
//        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
//        try (FileInputStream fileInputStream = new FileInputStream(encryptedFile)) {
//            byte[] encryptedBytes = fileInputStream.readAllBytes();
//            byte[] decryptedBytes = aesCipher.doFinal(encryptedBytes);
//            File file = convertByteArrayToFile(decryptedBytes, encryptedFile.getName());
//            MinIoUtil.deleteFile("keys",encryptedFile.getName()+".key");
//        }
//    }

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