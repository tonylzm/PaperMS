package usts.paperms.paperms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultPageController {

    @GetMapping("/")
    public String defaultPage() {
        // 这里可以添加任何要传递到模板的数据
        // 如果不需要传递数据，可以省略这个方法，直接返回模板名称即可
        return "file"; // 返回模板的名称，Spring Boot会自动寻找并加载templates目录下的对应模板文件
    }
    @GetMapping("/1")
    public String defaultPage1() {
        // 这里可以添加任何要传递到模板的数据
        // 如果不需要传递数据，可以省略这个方法，直接返回模板名称即可
        return "files"; // 返回模板的名称，Spring Boot会自动寻找并加载templates目录下的对应模板文件
    }
}

