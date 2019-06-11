package com.example.thousandschnapsen;

import com.example.thousandschnapsen.bluetooth.bluetoothLibraies.GameActivity;
import com.example.thousandschnapsen.defs.MessageInfo;
import com.example.thousandschnapsen.enums.GAME_MESSAGE_TYPE;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.stream.Collectors;

public class BluetoothMessageBroadcaster extends GameActivity implements MessageBroadcastInterface {
    Queue<MessageInfo> messages=new ArrayDeque<MessageInfo>();

    Integer playerNr;

    public BluetoothMessageBroadcaster(Integer playerNr) {
        this.playerNr = playerNr;
    }


    @Override
    public void bluetoothMessage(String message) {

    }

    @Override
    public void sendMessage(GAME_MESSAGE_TYPE messageType, String toJson, String serverName) {
        sendMessage(messageType, toJson,false,null, serverName);
    }

    @Override
    public void sendMessage(GAME_MESSAGE_TYPE messageType, String toJson, Boolean alreadyProcessed, GameSingleForm sender, String serverName) {
        String data = messageType.toString() + "||" + toJson + "||" + alreadyProcessed.toString();
        sendStringMessageForAll(data);
    }

    @Override
    public Queue<MessageInfo> avaitingMessagesInQueue() {
        return messages.stream().filter(p->p.getProcessed()!=null && !( p.getProcessed(playerNr)!=null &&  p.getProcessed(playerNr))  ).collect(Collectors.toCollection(ArrayDeque::new));
    }

    @Override
    public void addToQueue(MessageInfo messageInfo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clientConnectionSuccessEvent() {

    }

    @Override
    public void clientConnectionFailEvent(String serverAddress) {

    }

    @Override
    public void serverConnectionSuccessEvent(String clientAddress) {

    }

    @Override
    public void serverConnectionFailEvent(String clientAddress) {

    }

}
