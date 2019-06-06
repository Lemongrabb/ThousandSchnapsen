package com.example.thousandschnapsen.bluetooth.bluetoothLibraies.eventBus;

public class HowManyPlayersEvent {
    private final int numberOfPlayersEvent;

    public int getNumberOfPlayersEvent() {
        return numberOfPlayersEvent;
    }

    public HowManyPlayersEvent(int numberOfPlayers) {
        this.numberOfPlayersEvent = numberOfPlayers;
    }
}
