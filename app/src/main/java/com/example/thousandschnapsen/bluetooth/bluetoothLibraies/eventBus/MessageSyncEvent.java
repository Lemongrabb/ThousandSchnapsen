package com.example.thousandschnapsen.bluetooth.bluetoothLibraies.eventBus;

public class MessageSyncEvent {
    private final String syncMessage;
    private final String senderMacAddress;

    public MessageSyncEvent(String syncMessage, String senderMacAddress) {
        this.syncMessage = syncMessage;
        this.senderMacAddress = senderMacAddress;
    }

    public String getSyncMessage() {
        return syncMessage;
    }

    public String getSenderMacAddress() {
        return senderMacAddress;
    }
}
