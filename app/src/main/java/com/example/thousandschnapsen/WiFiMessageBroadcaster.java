package com.example.thousandschnapsen;


import androidx.appcompat.app.AppCompatActivity;

import com.example.thousandschnapsen.defs.MessageInfo;
import com.example.thousandschnapsen.enums.GAME_MESSAGE_TYPE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.stream.Collectors;

public class WiFiMessageBroadcaster extends AppCompatActivity implements MessageBroadcastInterface {
    Queue<MessageInfo> messages=new ArrayDeque<MessageInfo>();

    Integer playerNr;
    private Socket socket;
    private OutputStream outputStream;

    @Override
    public void sendMessage(GAME_MESSAGE_TYPE messageType, String toJson, String serverName) {
        sendMessage(messageType, toJson,false,null, serverName);
    }

    @Override
    public void sendMessage(GAME_MESSAGE_TYPE messageType, String toJson, Boolean alreadyProcessed, GameSingleForm sender, String serverName) {
        String data = messageType.toString() + "||" + toJson + "||" + alreadyProcessed.toString();
        try {
            outputStream.write((data).getBytes());
            outputStream.flush();
        } catch (IOException ex) {
        }
    }

    @Override
    public Queue<MessageInfo> avaitingMessagesInQueue() {
        return messages.stream().filter(p->p.getProcessed()!=null && !( p.getProcessed(playerNr)!=null &&  p.getProcessed(playerNr))  ).collect(Collectors.toCollection(ArrayDeque::new));    }

    @Override
    public void addToQueue(MessageInfo messageInfo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //inicializacja wątku klienckiego
    public void InitSocket(String server, int port) throws IOException {
        socket = new Socket(server, port);
        outputStream = socket.getOutputStream();

        Thread receivingThread = new Thread() {
            @Override
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line;
                } catch (IOException ex) {
                }
            }
        };
        receivingThread.start();
    }

}