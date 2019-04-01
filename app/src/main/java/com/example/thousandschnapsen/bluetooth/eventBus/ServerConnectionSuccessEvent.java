package com.example.thousandschnapsen.bluetooth.eventBus;

public class ServerConnectionSuccessEvent {
    private final String clientAddress;

    public String getClientAddress() {
        return clientAddress;
    }

    public ServerConnectionSuccessEvent(String clientAddress){
        this.clientAddress = clientAddress;
    }
}



