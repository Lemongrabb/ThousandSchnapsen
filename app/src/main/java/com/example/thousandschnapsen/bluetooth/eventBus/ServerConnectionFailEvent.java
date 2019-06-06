package com.example.thousandschnapsen.bluetooth.eventBus;

public class ServerConnectionFailEvent {
    private final String clientAddress;

    public String getClientAddress() {
        return clientAddress;
    }

    public ServerConnectionFailEvent(String clientAddress){
        this.clientAddress = clientAddress;
    }
}



