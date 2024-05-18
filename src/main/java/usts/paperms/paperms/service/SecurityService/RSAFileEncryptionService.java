package usts.paperms.paperms.service.SecurityService;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class RSAFileEncryptionService {
    @Autowired
    private RSAKeyGenerationService rsaKeyGenerationService;

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
    public File encryptFile(MultipartFile inputFile,String filename) throws Exception {
        // 生成AES密钥
        SecretKey aesKey = generateAESKey();
        // 将AES密钥加密
        byte[] encryptedAESKey = encryptAESKey(aesKey);
        // 加密文件内容
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
        File encryptedFile = new File(ENCRYPTED_FILE_DIRECTORY + filename);
        try (InputStream fileInputStream = inputFile.getInputStream();
             OutputStream fileOutputStream = new FileOutputStream(encryptedFile)) {
            byte[] inputBytes = fileInputStream.readAllBytes();
            byte[] encryptedBytes = aesCipher.doFinal(inputBytes);
            fileOutputStream.write(encryptedBytes);
        }
        // 将加密后的AES密钥写入文件
        try (OutputStream fileOutputStream = new FileOutputStream(AES_KEY_FILE_PATH +  filename + ".key")) {
            fileOutputStream.write(encryptedAESKey);
        }
        return encryptedFile;
    }
    //加密文件（文件为byte[]类型）
    public File encryptFiles(byte[] inputFile,String filename) throws Exception {
        // 生成AES密钥
        SecretKey aesKey = generateAESKey();
        // 将AES密钥加密
        byte[] encryptedAESKey = encryptAESKey(aesKey);
        // 加密文件内容
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
        File encryptedFile = new File(ENCRYPTED_FILE_DIRECTORY + filename);
        try (InputStream fileInputStream = new ByteArrayInputStream(inputFile);
             OutputStream fileOutputStream = new FileOutputStream(encryptedFile)) {
            byte[] inputBytes = fileInputStream.readAllBytes();
            byte[] encryptedBytes = aesCipher.doFinal(inputBytes);
            fileOutputStream.write(encryptedBytes);
        }
        // 将加密后的AES密钥写入文件
        try (OutputStream fileOutputStream = new FileOutputStream(AES_KEY_FILE_PATH +  filename + ".key")) {
            fileOutputStream.write(encryptedAESKey);
        }
        return encryptedFile;
    }

    public void decryptFile(File encryptedFile) throws Exception {
        // 从文件中读取加密的AES密钥
        byte[] encryptedAESKey;
        try (FileInputStream fileInputStream = new FileInputStream(AES_KEY_FILE_PATH+encryptedFile.getName()+".key")) {
            encryptedAESKey = fileInputStream.readAllBytes();
        }

        // 使用RSA私钥解密AES密钥
        SecretKey aesKey = decryptAESKey(encryptedAESKey);

        // 解密文件内容并替换原加密文件
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
        try (FileInputStream fileInputStream = new FileInputStream(encryptedFile)) {
            byte[] encryptedBytes = fileInputStream.readAllBytes();
            byte[] decryptedBytes = aesCipher.doFinal(encryptedBytes);
            try(FileOutputStream fileOutputStream = new FileOutputStream(ENCRYPTED_FILE_DIRECTORY + encryptedFile.getName())) {
                fileOutputStream.write(decryptedBytes);
            }
            //将AES密钥文件删除
            Path path = Path.of(AES_KEY_FILE_PATH + encryptedFile.getName() + ".key");
            Files.delete(path);
        }
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
}