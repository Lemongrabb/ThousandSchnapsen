package com.example.thousandschnapsen.defs;

import com.example.thousandschnapsen.enums.GAME_MESSAGE_TYPE;

public class MessageInfo {
    GAME_MESSAGE_TYPE messageType;
    String jsonParametres;
    Boolean processed[];

    public MessageInfo(GAME_MESSAGE_TYPE messageType, String jsonParametres, Boolean processed) {
        this.messageType = messageType;
        this.jsonParametres = jsonParametres;
        this.processed = new Boolean[3];
    }

    public GAME_MESSAGE_TYPE getMessageType() {
        return messageType;
    }

    public void setMessageType(GAME_MESSAGE_TYPE messageType) {
        this.messageType = messageType;
    }

    public String getJsonParametres() {
        return jsonParametres;
    }

    public void setJsonParametres(String jsonParametres) {
        this.jsonParametres = jsonParametres;
    }

    public Boolean[] getProcessed() {
        return processed;
    }

    public void setProcessed(int nr,Boolean processed) {
        this.processed[nr] = processed;
    }

    public Boolean getProcessed(int nr) {
        return processed[nr];
    }

    public void setProcessed(Boolean[] processed) {
        this.processed = processed;
    }
}
