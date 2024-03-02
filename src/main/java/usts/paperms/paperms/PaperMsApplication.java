package usts.paperms.paperms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })

// 指定要扫描的包路径
@EntityScan(basePackages = "usts.paperms.paperms.entity")
public class PaperMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaperMsApplication.class, args);
    }

}
