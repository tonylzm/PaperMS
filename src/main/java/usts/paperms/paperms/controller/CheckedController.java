package usts.paperms.paperms.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usts.paperms.paperms.common.Result;
import usts.paperms.paperms.entity.SysFile;
import usts.paperms.paperms.service.SysFileService;

@RestController
@RequestMapping("/api/checked")
public class CheckedController {
    @Autowired
    private SysFileService sysFileService;

    @PostMapping("/classChecked")
    public ResponseEntity<String> classChecked(@RequestParam("fileName") String filename,
                                               @RequestParam("classCheck") String classCheck,
                                               @RequestParam("opinion") String opinion,
                                               @RequestParam("status") String status) throws Exception {

        sysFileService.updateClassCheckByFileName(filename, classCheck, opinion,status);
        return ResponseEntity.ok("Class check successful");
    }

    @PostMapping("/collegeChecked")
    public ResponseEntity<String> collegeChecked(@RequestParam("fileName") String filename,
                                                 @RequestParam("collegeCheck") String collegeCheck,
                                                 @RequestParam("opinion") String opinion,
                                                 @RequestParam("status") String status) {

        sysFileService.updateCollegeCheckByFileName(filename, collegeCheck, opinion,status);
        return ResponseEntity.ok("College check successful");
    }

    //返回系主任审核通过的文件
    @PostMapping("/findCheckedfile")
    public Result findCheckedfile(@RequestParam Integer pageNum,
                                  @RequestParam Integer pageSize,
                                  @RequestParam("college")String college,
                                  @RequestParam("status") String status) {
        Page<SysFile> page=sysFileService.findPageByClassCheck(pageNum,pageSize,status,college);
        return Result.success(page);
    }

}
