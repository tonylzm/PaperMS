package usts.paperms.paperms.controller;

// 导入需要的包和类
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import usts.paperms.paperms.entity.SysFile;
import usts.paperms.paperms.service.DecryptFileService;
import usts.paperms.paperms.service.RSAFileEncryptionService;
import usts.paperms.paperms.service.SysFileService;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Base64;



@RestController
@RequestMapping("/api/upload")
public class uploadController {
    private static final String UPLOAD_DIR = "src/main/resources/static/files/";
    private static final String PUBLIC_KEY_FILE_PATH = "src/main/resources/static/files/security/public.der";

    private static final String PRIVATE_KEY_FILE_PATH = "static/files/security/private.der";
    private static final String OUTPUT_DIRECTORY = "src/main/resources/static/files/enter/";

    private static final String AES_KEY_FILE_PATH = "src/main/resources/static/files/AESkey/11.pdf.key";


    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private RSAFileEncryptionService rsaFileEncryptionService;
    @Autowired
    private DecryptFileService DecryptFileService;

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(//@RequestParam("file") MultipartFile file,
                                                   @RequestParam("username") String username,
                                                   @RequestParam("from") String from,
                                                   @RequestParam("md5") String md5,
                                                   @RequestParam("encryptedFile") MultipartFile encryptedFile,
                                                   @RequestParam("key") String key,
                                                   @RequestParam("fileName") String filename
                                                  ) {
        if (encryptedFile.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload", HttpStatus.BAD_REQUEST);
        }


        try {
            if (filename.contains("..")) {
                return new ResponseEntity<>("Filename contains invalid path sequence", HttpStatus.BAD_REQUEST);
            }
            // Check if the file is a PDF
            if (!encryptedFile.getContentType().equalsIgnoreCase("application/pdf")) {
                return new ResponseEntity<>("Only PDF files are allowed", HttpStatus.BAD_REQUEST);
            }
           //encrypt file
            // Calculate MD5 checksum of the file

            MultipartFile files= DecryptFileService.decryptFile(encryptedFile,key);
            String md5Checksum = calculateMD5(files.getBytes());
            if(!md5.equals(md5Checksum)){
                return new ResponseEntity<>("MD5 checksum does not match", HttpStatus.BAD_REQUEST);
            }
            File encryptedFiles = rsaFileEncryptionService.encryptFile(files,filename);
            // Copy file to the target location
            Path targetLocation = Paths.get(UPLOAD_DIR).resolve(filename);
            Files.copy(encryptedFiles.toPath(), targetLocation);

            // Save file information to the database
            SysFile sysFile = new SysFile();
            sysFile.setName(filename);
            sysFile.setType(encryptedFile.getContentType());
            sysFile.setSize(encryptedFile.getSize());
            sysFile.setUrl(targetLocation.toString());
            sysFile.setMd5(md5Checksum);
            sysFile.setProduced(username);
            sysFile.setFromon(from);
            sysFile.setDelete(false);
            sysFile.setEnable(true);
            sysFileService.save(sysFile);

            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
        } catch (IOException ex) {
            return new ResponseEntity<>("Could not store the file. Please try again!", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/public")
    public ResponseEntity<String> getPublicKey() {
        try {
            // 读取公钥文件内容
            byte[] encodedKey = Files.readAllBytes(Paths.get(PUBLIC_KEY_FILE_PATH));
            // 将公钥编码为Base64字符串，以便在HTTP响应中发送
            String publicKey = Base64.getEncoder().encodeToString(encodedKey);
            // 返回公钥给前端
            return ResponseEntity.ok(publicKey);
        } catch (IOException e) {
            // 处理文件读取异常
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            // 处理其他异常
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }


    //发送AES密钥
    @GetMapping("/aeskey")
    public ResponseEntity<String> getAESKey() {
        try {
            // 读取AES密钥文件内容
            byte[] encodedKey = Files.readAllBytes(Paths.get(AES_KEY_FILE_PATH));
            // 将AES密钥编码为Base64字符串，以便在HTTP响应中发送
            String aesKey = Base64.getEncoder().encodeToString(encodedKey);
            // 返回AES密钥给前端
            return ResponseEntity.ok(aesKey);
        } catch (IOException e) {
            // 处理文件读取异常
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            // 处理其他异常
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    // Method to calculate MD5 checksum
    private String calculateMD5(byte[] bytes) {
        return DigestUtils.md5Hex(bytes);
    }

}

