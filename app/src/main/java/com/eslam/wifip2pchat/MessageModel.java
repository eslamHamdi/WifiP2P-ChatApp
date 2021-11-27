package com.eslam.wifip2pchat;

public class MessageModel {

    private String body;
    private String type;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }



    public MessageModel(String body, String type) {
        this.body = body;
        this.type = type;
    }
}
