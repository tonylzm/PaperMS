package usts.paperms.paperms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usts.paperms.paperms.common.Result;
import usts.paperms.paperms.entity.Log;
import usts.paperms.paperms.security.ValidateToken;
import usts.paperms.paperms.service.LogSaveService;
import usts.paperms.paperms.service.LogService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/log")
public class LogController {
    @Autowired
    private LogService logService;
    @Autowired
    private LogSaveService logSaveService;

    @ValidateToken
    @GetMapping("/logs")
    public Map<String, Object> getLogs() {
        return logService.getLogs();
    }

    @ValidateToken
    @GetMapping("/sys_logs")
    public Result getSysLogs(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        Page<Log> logs = logSaveService.findAll(pageNum, pageSize);
        return Result.success(logs);
    }

    @ValidateToken
    @GetMapping("/all_logs")
    public Result getAllLogs(){
        return Result.success(logSaveService.findAll());
    }
}
