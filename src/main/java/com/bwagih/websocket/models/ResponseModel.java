package com.bwagih.websocket.models;

public class ResponseModel {
    private String replyCode;
    private String replyMessage;

    public ResponseModel() {
        this.replyCode = "0";
        this.replyMessage = "Operation Done Successfully";
    }

    public String getReplyCode() {
        return replyCode;
    }

    public void setReplyCode(String replyCode) {
        this.replyCode = replyCode;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }
}
