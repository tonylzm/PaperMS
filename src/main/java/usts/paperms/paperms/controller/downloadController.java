package usts.paperms.paperms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.InputStreamResource;
import usts.paperms.paperms.common.MinIoUtil;
import usts.paperms.paperms.config.MinIoProperties;
import usts.paperms.paperms.security.ValidateToken;
import usts.paperms.paperms.service.LogSaveService;
import usts.paperms.paperms.service.SecurityService.RSAFileEncryptionService;
import usts.paperms.paperms.service.SysFileService;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/download")
public class downloadController {
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private MinIoProperties minIoProperties;
    @Autowired
    private RSAFileEncryptionService rsaFileEncryptionService;
    @Autowired
    private LogSaveService logSaveService;
    // 创建一个 Resource 对象来包装错误消息
    Resource errorResource = new ByteArrayResource("下载错误".getBytes());
    @Autowired
    private usts.paperms.paperms.service.pigeonholeService pigeonholeService;

    @ValidateToken
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String fileName) {
        // 解码文件名
        String decodedFileName;
        try {
            decodedFileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // 如果解码失败，则直接使用原始文件名
            decodedFileName = fileName;
        }
        // 查询数据库中的MD5值
        String expectedMD5 = sysFileService.findMD5ByFileName(decodedFileName);
        if (expectedMD5 == null) {
            // 如果文件名不存在于数据库中，返回404 Not Found
            return ResponseEntity.notFound().build();
        }
        try {
            // 从 MinIO 下载文件并计算实际MD5值
            InputStream fileStream = MinIoUtil.getFileStream(minIoProperties.getBucketName(), decodedFileName);
            String actualMD5 = calculateMD5(fileStream);

            // 检查MD5值是否匹配
            if (expectedMD5.equals(actualMD5)) {
                // 重置 InputStream 以重新读取文件内容
                fileStream = MinIoUtil.getFileStream(minIoProperties.getBucketName(), decodedFileName);
                Resource resource = new InputStreamResource(fileStream);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", getEncodedFilename(decodedFileName));

                logSaveService.saveLog("教务主管解密并下载了文件：" + decodedFileName, "教务主管");
                // 返回包含文件内容的响应实体
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                // 如果MD5值不匹配，返回错误响应
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(errorResource);
            }
        } catch (IOException | NoSuchAlgorithmException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //归档下载
    @ValidateToken
    @GetMapping("/pigeonholeDownload")
    public ResponseEntity<Resource> pigeonholeDownload(@RequestParam("filename") String fileName) throws Exception {
        rsaFileEncryptionService.pigeonholeDecrypt(fileName);
        String decodedFileName;
        try {
            decodedFileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // 如果解码失败，则直接使用原始文件名
            decodedFileName = fileName;
        }
        // 查询数据库中的MD5值
        String expectedMD5 = pigeonholeService.findMD5ByFileName(decodedFileName);
        if (expectedMD5 == null) {
            // 如果文件名不存在于数据库中，返回404 Not Found
            return ResponseEntity.notFound().build();
        }
        try {
            // 从 MinIO 下载文件并计算实际MD5值
            InputStream fileStream = MinIoUtil.getFileStream("pigeonhole", decodedFileName);
            String actualMD5 = calculateMD5(fileStream);
            // 检查MD5值是否匹配
            if (expectedMD5.equals(actualMD5)) {
                // 重置 InputStream 以重新读取文件内容
                fileStream = MinIoUtil.getFileStream("pigeonhole", decodedFileName);
                Resource resource = new InputStreamResource(fileStream);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", getEncodedFilename(decodedFileName));
                // 返回包含文件内容的响应实体
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                // 如果MD5值不匹配，返回错误响应
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(errorResource);
            }
        } catch (IOException | NoSuchAlgorithmException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 使用UTF-8编码对文件名进行URL编码
    private String getEncodedFilename(String filename) {
        try {
            return URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return filename;
        }
    }

    // 计算文件的MD5值
    private String calculateMD5(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[8192];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            digest.update(buffer, 0, len);
        }
        byte[] hash = digest.digest();
        BigInteger bigInt = new BigInteger(1, hash);
        return bigInt.toString(16);
    }
}
