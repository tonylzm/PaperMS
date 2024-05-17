package usts.paperms.paperms.controller;

// 导入需要的包和类
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import usts.paperms.paperms.entity.SysFile;
import usts.paperms.paperms.security.PasswordEncryptionService;
import usts.paperms.paperms.service.LogSaveService;
import usts.paperms.paperms.service.SecurityService.DecryptFileService;
import usts.paperms.paperms.service.SecurityService.RSAFileEncryptionService;
import usts.paperms.paperms.service.SysFileService;
import usts.paperms.paperms.service.TimeService;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Base64;
import java.util.Date;


@RestController
@RequestMapping("/api/upload")
public class uploadController {
    //private static final String UPLOAD_DIR = "src/main/resources/static/files/";
    @Value("${spring.servlet.multipart.location}")
    private String UPLOAD_DIR;
    //private static final String PUBLIC_KEY_FILE_PATH = "src/main/resources/static/files/security/public.der";
    @Value("${service.publickey-dir}")
    private String PUBLIC_KEY_FILE_PATH;

    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private RSAFileEncryptionService rsaFileEncryptionService;
    @Autowired
    private DecryptFileService DecryptFileService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private PasswordEncryptionService passwordEncryption;
    @Autowired
    private TimeService timeService;
    @Autowired
    private LogSaveService logSaveService;

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("username") String username,
                                                   @RequestParam("from") String from,
                                                   @RequestParam("md5") String md5,
                                                   @RequestParam("encryptedFile") MultipartFile encryptedFile,
                                                   @RequestParam("fileName") String filename,
                                                   @RequestParam("aesKey") String aesKey,
                                                   @RequestParam("info") String info
                                                  ) throws Exception {

        if (encryptedFile.isEmpty()) {
            return new ResponseEntity<>("没有接收到文件资料", HttpStatus.BAD_REQUEST);
        }
        //将信息中的时间组合
       
        String checkStatus = sysFileService.findCheckByFileName(filename).orElse("未审核");

        if(!checkStatus.equals("未审核")&& !checkStatus.contains("不通过")){
            return new ResponseEntity<>("文件正在审核，请不要重复提交！", HttpStatus.BAD_REQUEST);
        }
        try {
            if (filename.contains("..")) {
                return new ResponseEntity<>("文件名包含无效的路径序列", HttpStatus.BAD_REQUEST);
            }
            // 检查文件是否为 PDF
            if (!encryptedFile.getContentType().equalsIgnoreCase("application/pdf")) {
                return new ResponseEntity<>("只接收PDF格式文件，请检查", HttpStatus.BAD_REQUEST);
            }
            JSONObject data = new JSONObject(info);
            String classCheck = data.getString("classCheck");
            String collegeCheck = data.getString("collegeCheck");
            //格式化时间,改成yyyy-MM-dd HH:mm:ss
            String time=timeService.time(data.getString("startTime"),data.getString("endTime"));
            // 计算文件的 MD5 校验和
            String decryptedAesKeyString = DecryptFileService.decryptAesKeyToString(aesKey);
            String hashedPassword= redisTemplate.opsForValue().get("hashedPassword:" + username);
            //输出hashedPassword
            System.out.println(hashedPassword);
            String password= DecryptFileService.decryptAesKeyToString(hashedPassword);
            if(!password.equals(decryptedAesKeyString)){
                return new ResponseEntity<>("AES密钥匹配失败", HttpStatus.BAD_REQUEST);
            }
            MultipartFile files= DecryptFileService.decryptFile(encryptedFile,decryptedAesKeyString);
            String md5Checksum = DecryptFileService.calculateMD5(files.getBytes());
            if(!md5.equals(md5Checksum)){
                return new ResponseEntity<>("文件完整性检查不通过", HttpStatus.BAD_REQUEST);
            }
            File encryptedFiles = rsaFileEncryptionService.encryptFile(files,filename);
            // 将文件复制到目标位置
            Path targetLocation = Paths.get(UPLOAD_DIR).resolve(filename);
            Files.copy(encryptedFiles.toPath(), targetLocation);
            SysFile sysFile = new SysFile();
            sysFile.setName(filename);
            sysFile.setType(encryptedFile.getContentType());
            sysFile.setSize(encryptedFile.getSize());
            sysFile.setUrl(targetLocation.toString());
            sysFile.setMd5(md5Checksum);
            sysFile.setProduced(username);
            sysFile.setDecrypt(false);

            sysFile.setTestname(data.getString("name"));
            sysFile.setTesttime(time);
            sysFile.setTesttype(data.getString("region"));
            sysFile.setClasses(data.getString("class"));
            sysFile.setCollege(data.getString("college"));
            sysFileService.save(sysFile,classCheck,collegeCheck);
            logSaveService.saveLog("用户"+username+"上传了"+filename+"文件",data.getString("realName"));
            return new ResponseEntity<>("文件上传成功", HttpStatus.OK);
        } catch (IOException ex) {
            return new ResponseEntity<>("无法存储文件，请再试一次！", HttpStatus.INTERNAL_SERVER_ERROR);
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

}

