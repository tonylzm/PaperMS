package usts.paperms.paperms.service.SecurityService;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class JsonConverter {
    // 将键值对转换为 JSON 字符串
    public static String convertToJson(Map<String, Object> data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    // 创建一个空的键值对
    public static Map<String, Object> createMap() {
        return new HashMap<>();
    }

    // 向键值对中添加键值对
    public static void addToMap(Map<String, Object> data, String key, Object value) {
        data.put(key, value);
    }
}
