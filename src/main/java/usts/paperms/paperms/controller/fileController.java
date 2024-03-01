package usts.paperms.paperms.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usts.paperms.paperms.common.Result;
import usts.paperms.paperms.entity.SysFile;
import usts.paperms.paperms.service.SysFileService;


import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class fileController {
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
}
