package com.bwagih.websocket.controller;

import com.bwagih.websocket.service.NotificationService;
import com.bwagih.websocket.dto.Message;
import com.bwagih.websocket.dto.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;

@Controller
public class MessageController {
    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/message") //for receiving Messages (ws/message)
    @SendTo("/topic/messages")  // to sending the messages back on a topic (/topic/messages)
    public ResponseMessage getMessage(final Message message) throws InterruptedException {
        Thread.sleep(1000);  //sleeping for 1 sec so to simulate just some wait time :)
        notificationService.sendGlobalNotification();
        return new ResponseMessage(HtmlUtils.htmlEscape(message.getMessageContent()));  //return response msg (with just escaping any special characters that might be received here in this message that we are listening on this endpoint and we are sending it back as the response basically our frontend is going to be taking caree of the rest :) )
    }

    @MessageMapping("/private-message")
    @SendToUser("/topic/private-messages")
    public ResponseMessage getPrivateMessage(final Message message,
                                             final Principal principal) throws InterruptedException {
        Thread.sleep(1000);
        notificationService.sendPrivateNotification(principal.getName());
        return new ResponseMessage(HtmlUtils.htmlEscape(
                "Sending private message to user " + principal.getName() + ": "
                        + message.getMessageContent())
        );
    }
}
