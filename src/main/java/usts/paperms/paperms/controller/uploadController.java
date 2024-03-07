package usts.paperms.paperms.controller;

// 导入需要的包和类
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import usts.paperms.paperms.entity.SysFile;
import usts.paperms.paperms.service.RSAFileEncryptionService;
import usts.paperms.paperms.service.SysFileService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



@RestController
@RequestMapping("/api/upload")
public class uploadController {
    private static final String UPLOAD_DIR = "src/main/resources/static/files/";

    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private RSAFileEncryptionService rsaFileEncryptionService;

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload", HttpStatus.BAD_REQUEST);
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                return new ResponseEntity<>("Filename contains invalid path sequence", HttpStatus.BAD_REQUEST);
            }
            // Check if the file is a PDF
            if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
                return new ResponseEntity<>("Only PDF files are allowed", HttpStatus.BAD_REQUEST);
            }
           //encrypt file
            // Calculate MD5 checksum of the file
            String md5Checksum = calculateMD5(file.getBytes());

            File encryptedFile = rsaFileEncryptionService.encryptFile(file);
            // Copy file to the target location
            Path targetLocation = Paths.get(UPLOAD_DIR).resolve(fileName);
            Files.copy(encryptedFile.toPath(), targetLocation);

            // Save file information to the database
            SysFile sysFile = new SysFile();
            sysFile.setName(fileName);
            sysFile.setType(file.getContentType());
            sysFile.setSize(file.getSize());
            sysFile.setUrl(targetLocation.toString());
            sysFile.setMd5(md5Checksum);
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

    // Method to calculate MD5 checksum
    private String calculateMD5(byte[] bytes) {
        return DigestUtils.md5Hex(bytes);
    }
}

