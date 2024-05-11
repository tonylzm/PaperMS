package usts.paperms.paperms.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/log")
public class LogController {

    @GetMapping("/logs")
    public Map<String, Object> getLogs() {
        Map<String, Object> logData = new HashMap<>();
        StringBuilder warn_logs = new StringBuilder();
        StringBuilder logs = new StringBuilder();
        int warnCount = 0;
        int errorCount = 0;
        int databaseCount = 0;
        int allCount = 0;
        String logFilePath = "/PaperMS/PaperMS/src/main/resources/server_log.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                allCount++;
                if (line.contains("WARN")) {
                    warn_logs.append(line).append("\n");
                    warnCount++;
                } else if (line.contains("ERROR")) {
                    logs.append(line).append("\n");
                    errorCount++;
                }else if(line.contains("DEBUG")) {
                    databaseCount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle file reading error
            logs.append("Error reading log file: ").append(e.getMessage());
        }

        logData.put("logs", logs.toString());
        logData.put("warn_logs", warn_logs.toString());
        logData.put("warnCount", warnCount);
        logData.put("errorCount", errorCount);
        logData.put("allCount", allCount);
        logData.put("databaseCount", databaseCount);

        return logData;
    }
}
