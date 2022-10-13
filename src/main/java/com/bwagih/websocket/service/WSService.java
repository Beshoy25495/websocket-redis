package com.bwagih.websocket.service;

import com.bwagih.websocket.dto.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WSService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final RedisMessagePublisher redisMessagePublisher;
    private final RedisMessageSubscriber redisMessageSubscriber;

    @Autowired
    public WSService(SimpMessagingTemplate messagingTemplate, NotificationService notificationService
            , RedisMessagePublisher redisMessagePublisher , RedisMessageSubscriber redisMessageSubscriber) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
        this.redisMessagePublisher = redisMessagePublisher;
        this.redisMessageSubscriber = redisMessageSubscriber;
    }

    public void notifyFrontend(final String message) {
        ResponseMessage response = new ResponseMessage(message);
        notificationService.sendGlobalNotification();

        messagingTemplate.convertAndSend("/topic/messages"
                , response);

    }

    public void notifyUser(final String id, final String message) {
        ResponseMessage response = new ResponseMessage(message);

        notificationService.sendPrivateNotification(id);

        messagingTemplate.convertAndSendToUser(id, "/topic/private-messages", response);
    }

    //this Method notify frontEnd at existed key change
    public void notifyFrontendKeyChanged(final String message) {
        ResponseMessage response = new ResponseMessage(message);
        notificationService.sendGlobalNotification();

        redisMessagePublisher.publish(response.getContent());

//        RedisMessageSubscriber.messageList.get(0).contains(message);

        messagingTemplate.convertAndSend("/topic/key-change"
                , response.getContent());

    }

    //this Method notify frontEnd at new key created
    public void notifyFrontendKeyEvent(final String message) {
        ResponseMessage response = new ResponseMessage(message);
        notificationService.sendGlobalNotification();

        redisMessagePublisher.publish(response.getContent());

//        RedisMessageSubscriber.messageList.get(0).contains(message);

        messagingTemplate.convertAndSend("/topic/key-event"
                , response.getContent());

    }

}
