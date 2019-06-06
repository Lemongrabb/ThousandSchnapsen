package com.example.thousandschnapsen.bluetooth.eventBus;

public class ClientConnectionFailEvent {
    private final String serverAddress;

    public String getServerAddress() {
        return serverAddress;
    }

    public ClientConnectionFailEvent(String serverAddress) {
        this.serverAddress = serverAddress;
    }
}
