package usts.paperms.paperms.common;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@Component
public class MessageScheduler {

    private final SimpMessagingTemplate messagingTemplate;

    public MessageScheduler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    //@Scheduled(fixedRate = 5000) // 每5秒执行一次
    public void sendPeriodicMessage() {
        // 向 WebSocket 目的地发送消息
        messagingTemplate.convertAndSend("/topic/messages", "这是一个自动广播消息，当前时间：" + new Date());
    }
}


