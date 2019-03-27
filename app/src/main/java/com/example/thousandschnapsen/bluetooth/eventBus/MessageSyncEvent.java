package com.example.thousandschnapsen.bluetooth.eventBus;

public class MessageSyncEvent {
    private final String syncMessage;


    public String getSyncMessage() {
        return syncMessage;
    }

    public MessageSyncEvent(String syncMessage) {
        this.syncMessage = syncMessage;
    }
}
