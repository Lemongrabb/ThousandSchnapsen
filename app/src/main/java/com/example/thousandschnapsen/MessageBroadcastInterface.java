package com.example.thousandschnapsen;

import com.example.thousandschnapsen.defs.MessageInfo;
import com.example.thousandschnapsen.enums.GAME_MESSAGE_TYPE;

import java.util.Queue;

public interface MessageBroadcastInterface {
    void sendMessage(GAME_MESSAGE_TYPE message, String toJson, String serverName);
    void sendMessage(GAME_MESSAGE_TYPE messageType, String toJson, Boolean alreadyProcessed, GameSingleForm sender, String serverName);
    Queue<MessageInfo> avaitingMessagesInQueue();
    void addToQueue(MessageInfo messageInfo);
}
