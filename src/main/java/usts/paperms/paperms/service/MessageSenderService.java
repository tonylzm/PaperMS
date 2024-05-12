package usts.paperms.paperms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MessageSenderService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendMessage(String message) {
        // 发送消息到指定的目标路径
        messagingTemplate.convertAndSend("/topic/messages", message);
    }

}

