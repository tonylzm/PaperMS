//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.File;
//
//@RestController
//@RequestMapping("/encryption")
//public class EncryptionController {
//
//    @Autowired
//    private RSAFileEncryptionService rsaFileEncryptionService;
//
//    @PostMapping("/encrypt")
//    public String encryptFile(@RequestParam("file") MultipartFile file) {
//        try {
//            // 创建一个临时文件
//            File tempFile = File.createTempFile("temp", null);
//            // 将MultipartFile转换为File
//            file.transferTo(tempFile);
//
//            // 加密文件
//            File encryptedFile = File.createTempFile("encrypted", null);
//            rsaFileEncryptionService.encryptFile(tempFile, encryptedFile);
//
//            // 返回加密后的文件路径
//            return "File encrypted successfully. Encrypted file path: " + encryptedFile.getAbsolutePath();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Failed to encrypt file.";
//        }
//    }
//
//    @PostMapping("/decrypt")
//    public String decryptFile(@RequestParam("file") MultipartFile file) {
//        try {
//            // 创建一个临时文件
//            File tempFile = File.createTempFile("temp", null);
//            // 将MultipartFile转换为File
//            file.transferTo(tempFile);
//
//            // 解密文件
//            File decryptedFile = File.createTempFile("decrypted", null);
//            rsaFileEncryptionService.decryptFile(tempFile, decryptedFile);
//
//            // 返回解密后的文件路径
//            return "File decrypted successfully. Decrypted file path: " + decryptedFile.getAbsolutePath();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Failed to decrypt file.";
//        }
//    }
//}
