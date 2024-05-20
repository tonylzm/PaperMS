package usts.paperms.paperms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })

// 指定要扫描的包路径
@EntityScan(basePackages = "usts.paperms.paperms.entity")
@EnableScheduling
public class PaperMsApplication {
//    @Value("${key.upload-dir}")
//    private String keyUploadDir;
//    @Value("${spring.servlet.multipart.location}")
//    private String fileUploadDir;
//    @Value("${service.key-dir}")
//    private String keyDir;
    public static void main(String[] args) {
        SpringApplication.run(PaperMsApplication.class, args);
    }
//
//    @Bean
//    CommandLineRunner init() {
//        return args -> {
//            createDirectory(fileUploadDir);
//            createDirectory(keyUploadDir);
//            createDirectory(keyDir);
//        };
//    }
//
//    private void createDirectory(String path) {
//        File directory = new File(path);
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//    }
}
