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
import usts.paperms.paperms.service.LogSaveService;
import usts.paperms.paperms.service.SysFileService;

@RestController
@RequestMapping("/api/checked")
public class CheckedController {
    @Autowired
    private SysFileService sysFileService;
    @Autowired
    private LogSaveService logSaveService;

    //系主任审核
    @PostMapping("/classChecked")
    public ResponseEntity<String> classChecked(@RequestParam("fileName") String filename,
                                               @RequestParam("classCheck") String classCheck,
                                               @RequestParam("opinion") String opinion,
                                               @RequestParam("status") String status) throws Exception {

        sysFileService.updateClassCheckByFileName(filename, classCheck,opinion,status);
        logSaveService.saveLog("系主任进行了对"+filename+"的审批",classCheck);
        return ResponseEntity.ok("Class check successful");
    }
    //院长审核
    @PostMapping("/collegeChecked")
    public ResponseEntity<String> collegeChecked(@RequestParam("fileName") String filename,
                                                 @RequestParam("collegeCheck") String collegeCheck,
                                                 @RequestParam("opinion") String opinion,
                                                 @RequestParam("status") String status) {

        sysFileService.updateCollegeCheckByFileName(filename, collegeCheck, opinion,status);
        logSaveService.saveLog("院长进行了对"+filename+"的审批",collegeCheck);
        return ResponseEntity.ok("College check successful");
    }

    //返回需要审核的文件(已经弃用）
    @PostMapping("/findCheckedfile")
    public Result findCheckedfile(@RequestParam Integer pageNum,
                                  @RequestParam Integer pageSize,
                                  @RequestParam("college")String college,
                                  @RequestParam("status") String status,
                                  @RequestParam("produced")String produced,
                                  @RequestParam(defaultValue = "") String name) {
        Page<SysFile> page=sysFileService.findPageByClassCheck(pageNum,pageSize,status,college,name,produced);
        return Result.success(page);
    }

    //返回文件(已经弃用）
    @PostMapping("/findFile")
    public Result findFile(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam("produced")String produced,
                           @RequestParam(defaultValue = "") String name) {
        Page<SysFile> page = sysFileService.findAllFilesWithCheckStatus( pageNum, pageSize,produced, name);
        return Result.success(page);
    }

    //返回class_check对应需要审核的文件
    @PostMapping("/findClassCheckFile")
    public Result findClassCheckFile(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize,
                                     @RequestParam("class_check")String class_check,
                                     @RequestParam("status") String status,
                                     @RequestParam("college")String college,
                                     @RequestParam(defaultValue = "") String name) {
        Page<SysFile> page = sysFileService.findALLFilesWithCheckClass( pageNum, pageSize, name, class_check,status,college);
        logSaveService.saveLog("系主任"+class_check+"查看了"+college+"的文件",class_check);
        return Result.success(page);
    }
    //返回college_check对应需要审核的文件
    @PostMapping("/findCollegeCheckFile")
    public Result findCollegeCheckFile(@RequestParam Integer pageNum,
                                     @RequestParam Integer pageSize,
                                     @RequestParam("college_check")String college_check,
                                     @RequestParam("status") String status,
                                     @RequestParam("college")String college,
                                     @RequestParam(defaultValue = "") String name) {
        Page<SysFile> page = sysFileService.findALLFilesWithCheckCollege( pageNum, pageSize, name, college_check,status,college);
        logSaveService.saveLog("院长"+college_check+"查看了"+college+"的文件",college_check);
        return Result.success(page);
    }
}
