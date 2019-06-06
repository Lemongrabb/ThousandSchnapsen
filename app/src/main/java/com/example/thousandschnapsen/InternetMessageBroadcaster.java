package com.example.thousandschnapsen;

import com.example.thousandschnapsen.defs.MessageInfo;
import com.example.thousandschnapsen.enums.GAME_MESSAGE_TYPE;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.stream.Collectors;

import androidx.appcompat.app.AppCompatActivity;

public class InternetMessageBroadcaster extends AppCompatActivity implements MessageBroadcastInterface {
    Queue<MessageInfo> messages=new ArrayDeque<MessageInfo>();

    Integer playerNr;
    private Socket mSocket;

    public InternetMessageBroadcaster(Integer playerNr, Socket mSocket) {
        this.playerNr = playerNr;
        this.mSocket = mSocket;

        //RECEIVING DATA FROM THE OTHERS CLIENTS IN CURRENT GAME
        mSocket.on("internetGameData", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String[] recievedMessageInfo = ((String) args[0]).split("||"); //RECEIVED DATA FROM THE OTHERS CLIENT IN CURRENT GAME

                        GAME_MESSAGE_TYPE messageType = GAME_MESSAGE_TYPE.valueOf(recievedMessageInfo[0]);
                        String toJson = recievedMessageInfo[1];
                        Boolean processed = Boolean.parseBoolean(recievedMessageInfo[2]);

                        MessageInfo messageInfo = new MessageInfo(messageType, toJson, processed);
                        messages.add(messageInfo);
                    }
                });
            }
        });
    }

    @Override
    public  void sendMessage(GAME_MESSAGE_TYPE messageType, String toJson, String serverName) {
        sendMessage(messageType, toJson,false,null, serverName);
    }

    @Override
    public  void sendMessage(GAME_MESSAGE_TYPE messageType, String toJson, Boolean alreadyProcessed, GameSingleForm sender, String serverName) {
        String data = messageType.toString() + "||" + toJson + "||" + alreadyProcessed.toString();
        mSocket.emit("internetGameData", serverName, data);
    }


    @Override
    public Queue<MessageInfo> avaitingMessagesInQueue() {
        return messages.stream().filter(p->p.getProcessed()!=null && !( p.getProcessed(playerNr)!=null &&  p.getProcessed(playerNr))  ).collect(Collectors.toCollection(ArrayDeque::new));
    }


    @Override
    public void addToQueue(MessageInfo messageInfo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
