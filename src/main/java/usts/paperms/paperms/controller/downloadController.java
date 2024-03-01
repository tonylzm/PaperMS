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
import org.springframework.core.io.UrlResource;
import usts.paperms.paperms.service.SysFileService;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/download")
public class downloadController {
    @Autowired
    private SysFileService sysFileService;
    private static final String FILE_DIRECTORY = "src/main/resources/static/files/";
    // 省略其他注入和方法
// 创建一个 Resource 对象来包装错误消息
    Resource errorResource = new ByteArrayResource("下载错误".getBytes());
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

        // 解析文件路径
        Path filePath = Paths.get(FILE_DIRECTORY).resolve(decodedFileName).normalize();
        try {
            // 计算文件的实际MD5值
            String actualMD5 = calculateMD5(filePath);

            // 检查MD5值是否匹配
            if (expectedMD5.equals(actualMD5)) {
                // 加载文件作为资源
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists() || resource.isReadable()) {
                    // 设置响应头，包括文件名的编码
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    headers.setContentDispositionFormData("attachment", getEncodedFilename(decodedFileName));

                    // 返回包含文件内容的响应实体
                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(resource);
                }
            } else {
                // 如果MD5值不匹配，返回错误响应
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(errorResource);
            }
        } catch (IOException | NoSuchAlgorithmException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        // 如果文件不存在或不可读，返回404 Not Found
        return ResponseEntity.notFound().build();
    }

    // 省略其他方法
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
    private String calculateMD5(Path filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        try (InputStream is = Files.newInputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
        }
        byte[] hash = digest.digest();
        BigInteger bigInt = new BigInteger(1, hash);
        return bigInt.toString(16);
    }
}
