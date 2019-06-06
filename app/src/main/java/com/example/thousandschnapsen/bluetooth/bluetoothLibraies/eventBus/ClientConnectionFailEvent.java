package com.example.thousandschnapsen.bluetooth.bluetoothLibraies.eventBus;

public class ClientConnectionFailEvent {
    private final String serverAddress;

    public String getServerAddress() {
        return serverAddress;
    }

    public ClientConnectionFailEvent(String serverAddress) {
        this.serverAddress = serverAddress;
    }
}
