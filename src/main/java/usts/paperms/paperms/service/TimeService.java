package usts.paperms.paperms.service;

import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class TimeService {
    public String time(String startTimeStr, String endTimeStr) {
        // 定义日期时间格式化对象
        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdfOutputTime = new SimpleDateFormat("HH:mm");
        try {
            // 解析 startTime 和 endTime 字符串为 Date 对象
            Date startTime = sdfInput.parse(startTimeStr);
            Date endTime = sdfInput.parse(endTimeStr);
            // 格式化 Date 对象为所需的格式
            String formattedStartTime = sdfOutput.format(startTime);
            String formattedEndTime = sdfOutputTime.format(endTime);
            // 输出格式化后的时间
            return formattedStartTime + " - " + formattedEndTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
