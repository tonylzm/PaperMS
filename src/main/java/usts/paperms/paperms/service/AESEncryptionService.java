package usts.paperms.paperms.service;

import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class AESEncryptionService {

    private static final String PRIVATE_KEY_FILE_PATH = "src/main/resources/static/files/security/keyfile.key";
    private static final String ENCRYPTED_FILE_DIRECTORY = "src/main/resources/static/files/enter/";

    // 加密文件
    public void encryptFile(File inputFile) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
        // Read input file
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        byte[] inputBytes = new byte[(int) inputFile.length()];
        fileInputStream.read(inputBytes);
        fileInputStream.close();

        SecretKey secretKey = loadAESKeyFromFile(); // 加载AES密钥
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);


        // Encrypt file
        byte[] encryptedBytes = cipher.doFinal(inputBytes);
        // Define the output file path for the encrypted file
        File encryptedFile = new File(ENCRYPTED_FILE_DIRECTORY+inputFile.getName());

        // Write encrypted data to file
        FileOutputStream fileOutputStream = new FileOutputStream(encryptedFile);
        fileOutputStream.write(encryptedBytes);
        fileOutputStream.close();

    }
    // 解密文件
    public void decryptFile(File encryptedFile) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {

        FileInputStream fileInputStream = new FileInputStream(encryptedFile);
        byte[] encryptedBytes = new byte[(int) encryptedFile.length()];
        fileInputStream.read(encryptedBytes);
        fileInputStream.close();

        SecretKey secretKey = loadAESKeyFromFile(); // 加载AES密钥
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        // Define the output file path for the decrypted file
        File decryptedFile = new File(ENCRYPTED_FILE_DIRECTORY+ encryptedFile.getName());

        // Write decrypted data to file
        FileOutputStream fileOutputStream = new FileOutputStream(decryptedFile);
        fileOutputStream.write(decryptedBytes);
        fileOutputStream.close();
    }

    // 从文件加载AES密钥
    private SecretKey loadAESKeyFromFile() throws IOException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(AESEncryptionService.PRIVATE_KEY_FILE_PATH));
        return new SecretKeySpec(keyBytes, "AES");
    }


}
