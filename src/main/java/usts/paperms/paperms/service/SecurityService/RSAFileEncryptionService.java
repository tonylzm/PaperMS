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


//    @Value("${public_key}")
//    private String publicKeyStr;
//    @Value("${private_key}")
//    private String privateKeyStr;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public RSAFileEncryptionService() {
        // Constructor remains empty
    }

    @PostConstruct
    public void init() throws Exception {
        checkAndGenerateKeys();
        privateKey = loadPrivateKey(PRIVATE_KEY_FILE_PATH);
        publicKey = loadPublicKey(PUBLIC_KEY_FILE_PATH);
    }

    private void checkAndGenerateKeys() throws Exception {
        File privateKeyFile = new File(PRIVATE_KEY_FILE_PATH);
        File publicKeyFile = new File(PUBLIC_KEY_FILE_PATH);

        if (!privateKeyFile.exists() || !publicKeyFile.exists()) {
            rsaKeyGenerationService.generateKeys();
        }
    }

    private PrivateKey loadPrivateKey(String filePath) throws Exception {
        FileInputStream fis = new FileInputStream(filePath);
        byte[] keyBytes = new byte[fis.available()];
        fis.read(keyBytes);
        fis.close();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    private PublicKey loadPublicKey(String filePath) throws Exception {
        FileInputStream fis = new FileInputStream(filePath);
        byte[] keyBytes = new byte[fis.available()];
        fis.read(keyBytes);
        fis.close();
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

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

    // 使用RSA公钥加密AES密钥
    private byte[] encryptAESKey(SecretKey aesKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(aesKey.getEncoded());
    }

    // 使用RSA私钥解密AES密钥
    private SecretKey decryptAESKey(byte[] encryptedAESKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedAESKey = cipher.doFinal(encryptedAESKey);
        return new SecretKeySpec(decryptedAESKey, "AES");
    }
}