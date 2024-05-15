package usts.paperms.paperms.controller;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import usts.paperms.paperms.service.MessageSenderService;
//
//
//@RestController
//@RequestMapping("/api/message")
//public class MessageController {
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//    @Autowired
//    private MessageSenderService messageSenderService;
//
//
//    @GetMapping("/send")
//    public void sendMessage() {
//        String message = "Hello from the server!";
//        messagingTemplate.convertAndSend("/topic/messages", message);
//        System.out.println("Message sent: " + message);
//
//    }
//}

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import usts.paperms.paperms.service.LogService;

import java.util.Date;
import java.util.Map;

@Controller
public class MessageController {
    private final SimpMessagingTemplate messagingTemplate;
    private boolean shouldBroadcast = false;
    @Autowired
    private LogService logService;

    public MessageController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    @MessageMapping("/hello") // 对应客户端发送的目的地 /app/hello
    public void handleHelloMessage(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> logData = logService.getLogs();
        messagingTemplate.convertAndSend("/topic/messages", logData);
        // 设置标志，准备开始广播
        //shouldBroadcast = true;

    }
    @MessageMapping("/byb") // 对应客户端发送的目的地 /app/hello
    public void handleBybMessage(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
        // 发送接收成功的消息给客户端
        messagingTemplate.convertAndSend("/topic/messages", "消息已成功接收");
        // 设置标志，准备开始广播
        shouldBroadcast = false;
    }

    @SubscribeMapping("/messages")
    public void subscribeToMessages() {
        // 当客户端订阅 /topic/messages 时，可以在这里处理相关逻辑
        messagingTemplate.convertAndSend("/topic/messages", "消息已成功订阅");;
    }

//    @Scheduled(fixedRate = 5000) // 每5秒执行一次
//    public void sendPeriodicMessage() {
//        // 向客户端发送周期性消息
//        if (shouldBroadcast) {
//            Map<String, Object> logData = logService.getLogs();
//            messagingTemplate.convertAndSend("/topic/messages", logData);
//        }
//    }
}
