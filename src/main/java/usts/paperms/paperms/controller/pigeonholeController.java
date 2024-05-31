package usts.paperms.paperms.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import usts.paperms.paperms.common.Result;
import usts.paperms.paperms.security.ValidateToken;
import usts.paperms.paperms.service.LogSaveService;
import usts.paperms.paperms.service.pigeonholeService;

@RestController
@RequestMapping("/api/pigeonhole")
public class pigeonholeController {
    @Autowired
    private pigeonholeService pigeonholeService;
    @Autowired
    private LogSaveService logSaveService;

    //通过学院查找所有的信箱
    @ValidateToken
    @GetMapping("/find_all")
    public Result findAllByCollege(String college, int pageNum, int pageSize) {
        return Result.success(pigeonholeService.findAllByCollege(college, pageNum, pageSize));
    }

    @ValidateToken
    @PostMapping("/delete")
    public Result delet(@RequestParam("fileName") String fileName){
        pigeonholeService.delet(fileName);
        logSaveService.saveLog("系统删除了文件：" + fileName, "System");
        return Result.success("删除成功");
    }

    @ValidateToken
    @PostMapping("/reupload")
    public Result reupload(@RequestParam("fileName") String fileName){
        pigeonholeService.delete(fileName);
        return Result.success("删除成功");
    }

}
