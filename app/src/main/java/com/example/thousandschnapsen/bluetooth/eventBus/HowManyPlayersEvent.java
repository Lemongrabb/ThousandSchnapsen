package com.example.thousandschnapsen.bluetooth.eventBus;

public class HowManyPlayersEvent {
    private final int numberOfPlayersEvent;

    public int getNumberOfPlayersEvent() {
        return numberOfPlayersEvent;
    }

    public HowManyPlayersEvent(int numberOfPlayers) {
        this.numberOfPlayersEvent = numberOfPlayers;
    }
}
