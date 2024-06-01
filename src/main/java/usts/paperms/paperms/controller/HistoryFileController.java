package usts.paperms.paperms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usts.paperms.paperms.common.Result;
import usts.paperms.paperms.entity.historychecked;
import usts.paperms.paperms.security.ValidateToken;
import usts.paperms.paperms.service.HistoryFileService;

@RestController
@RequestMapping("/api/history")
public class HistoryFileController {
    @Autowired
    private HistoryFileService historyFileService;

    //返回分页查找的历史文件
    @ValidateToken
    @PostMapping("/findHistoryfile")
    public ResponseEntity<Page> findHistoryfile(@RequestParam Integer pageNum,
                                          @RequestParam Integer pageSize,
                                          @RequestParam("produced")String produced,
                                          @RequestParam(defaultValue = "") String name) {
        Page<historychecked> page=historyFileService.findPageByProducedAndName(pageNum,pageSize,produced,name);
        return ResponseEntity.ok(page);
    }

    //返回学院的历史文件
    //@ValidateToken
    @GetMapping("/findCollegeHistoryfile")
    public Result findCollegeHistoryfile(String college) {
        return Result.success(historyFileService.findAllByCollege(college));
    }
}
