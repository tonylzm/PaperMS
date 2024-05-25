/**
 * 文件控制器
 * 用于处理文件上传、下载、预览、解密等操作
 * 编写者： tonylzm
 * 邮箱：3071247462@qq.com
 */
package usts.paperms.paperms.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usts.paperms.paperms.common.MinIoUtil;
import usts.paperms.paperms.common.Result;
import usts.paperms.paperms.config.MinIoProperties;
import usts.paperms.paperms.entity.SysFile;
import usts.paperms.paperms.security.ValidateToken;
import usts.paperms.paperms.service.LogSaveService;
import usts.paperms.paperms.service.SecurityService.RSAFileEncryptionService;
import usts.paperms.paperms.service.SysFileService;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
public class fileController {
    //private static final String DOCUMENTS_DIRECTORY ="src/main/resources/static/files/";
    @Value("${spring.servlet.multipart.location}")
    private String DOCUMENTS_DIRECTORY;
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private RSAFileEncryptionService rsaFileEncryptionService;
    @Autowired
    private LogSaveService logSaveService;
    @Autowired
    MinIoProperties minIoProperties;

    @ValidateToken
    @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)

    public  Result findPage(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            @RequestParam(defaultValue = "") String name) {
        // 调用 SysFileService 的方法执行分页查询
        Page<SysFile> page = sysFileService.findPage(pageNum, pageSize, name);
        // 构造返回结果
        return Result.success(page);
    }
    //将文件按照学院信息进行分页输出
    @ValidateToken
    @GetMapping(value = "/pageByCollege", produces = MediaType.APPLICATION_JSON_VALUE)
    public  Result findPageByCollege(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            @RequestParam(defaultValue = "") String college) {
        // 调用 SysFileService 的方法执行分页查询
        Page<SysFile> page = sysFileService.findPageByCollege(pageNum, pageSize, college);
        // 构造返回结果
        return Result.success(page);
    }

    @ValidateToken
    @GetMapping(value = "/pageByProduced", produces = MediaType.APPLICATION_JSON_VALUE)
    public  Result findPageByProduced(@RequestParam Integer pageNum,
                            @RequestParam Integer pageSize,
                            @RequestParam("produced") String produced,
                            @RequestParam(defaultValue = "") String name) {
        // 调用 SysFileService 的方法执行分页查询
        Page<SysFile> page = sysFileService.findPageByProduced(pageNum, pageSize, produced, name);
        // 构造返回结果
        return Result.success(page);
    }
    @ValidateToken
    @GetMapping(value = "/opinion", produces = MediaType.APPLICATION_JSON_VALUE)
    public  Result findopinion(@RequestParam Integer pageNum,
                                      @RequestParam Integer pageSize,
                                      @RequestParam("produced") String produced,
                                      @RequestParam("name") String name) {
        // 调用 SysFileService 的方法执行分页查询
        Page<SysFile> page = sysFileService.findPageByProduced(pageNum, pageSize, produced, name);
        // 构造返回结果
        return Result.success(page);
    }
    //文件预览方法
    @ValidateToken
    @GetMapping("/preview")
    public ResponseEntity<byte[]> previewDocument(@RequestParam("fileName") String fileName,
                                                  @RequestParam("Actor") String Actor){
        try {
            //minio文件存储系统
            InputStream fileInputStream = MinIoUtil.getFileStream(minIoProperties.getBucketName(),fileName);
            byte [] fileContent = fileInputStream.readAllBytes();
            logSaveService.saveLog(Actor+"预览了"+fileName,Actor);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //文件解密方法
    @ValidateToken
    @PostMapping("/decrypt")
    public ResponseEntity<String> decryptDocument(@RequestParam("fileName") String fileName,
                                                  @RequestParam("Actor") String Actor){
        try {
            rsaFileEncryptionService.decryptFiles(fileName);
            logSaveService.saveLog(Actor+"解密了"+fileName,Actor);
            //将相应文件在数据库中isDecrypted字段设置为false
            SysFile sysFile = sysFileService.findByName(fileName);
            sysFile.setDecrypt(true);
            sysFileService.saveDecrypt(sysFile);
            return ResponseEntity.ok("File decrypted successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred while decrypting the file", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //查询文件审核状态
    @ValidateToken
    @PostMapping("/check")
    public ResponseEntity<String> checkDocument(@RequestParam("fileName") String fileName) {
        // 调用 SysFileService 的方法查询文件审核状态
        String checkStatus = sysFileService.findCheckByFileName(fileName).orElse("待审核");
        return ResponseEntity.ok(checkStatus);
    }

}