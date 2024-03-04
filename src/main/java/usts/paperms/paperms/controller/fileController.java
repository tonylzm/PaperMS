package usts.paperms.paperms.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usts.paperms.paperms.common.Result;
import usts.paperms.paperms.entity.SysFile;
import usts.paperms.paperms.service.SysFileService;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class fileController {
    private static final String DOCUMENTS_DIRECTORY ="src/main/resources/static/files/";
    @Autowired
    private SysFileService sysFileService;
    @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)

    public  Result findPage(@RequestParam Integer pageNum,
                                  @RequestParam Integer pageSize,
                                  @RequestParam(defaultValue = "") String name) {
        // 调用 SysFileService 的方法执行分页查询
        Page<SysFile> page = sysFileService.findPage(pageNum, pageSize, name);


        // 构造返回结果
        return Result.success(page);
    }
    @GetMapping("/preview")
    public ResponseEntity<byte[]> previewDocument(@RequestParam("fileName") String fileName) {
        try {
            // 构建文件路径
            String filePath = DOCUMENTS_DIRECTORY + File.separator + fileName;

            // 检查文件是否存在
            if (!Files.exists(Paths.get(filePath))) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // 读取文件内容并返回给客户端
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
