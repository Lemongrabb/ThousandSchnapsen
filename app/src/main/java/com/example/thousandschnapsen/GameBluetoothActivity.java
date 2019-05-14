package com.example.thousandschnapsen;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thousandschnapsen.bluetooth.GameActivity;

import java.util.HashMap;


public class GameBluetoothActivity extends GameActivity {

    Button sendMessage;
    EditText messageText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendMessage = findViewById(R.id.button);
        messageText = findViewById(R.id.editText);

        //Przycisk do wysyłania wiadomości
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameReady) {
                    sendStringMessageForAll(messageText.getText().toString());
                    Log.d("Send message:", messageText.getText().toString());
                    messageText.getText().clear();
                }
            }
        });
    }

    //Wiadomosc przychodzaca
    @Override
    public void bluetoothMessage(String message) {
        toastMsg(message);
    }

    //Jesli udało się polaczyc sie z serwerm
    @Override
    public void clientConnectionSuccessEvent() {

    }

    //Jesli utracono lub nie udało sie polaczyc z serwerem
    @Override
    public void clientConnectionFailEvent(String serverAddress) {

    }

    //Jesli doszło pomyślnego polaczenia sie z klientem
    @Override
    public void serverConnectionSuccessEvent(String clientAddress) {

    }

    //Jesli doszło do przerwania polaczenia z klientem
    @Override
    public void serverConnectionFailEvent(String clientAddress) {

    }
}


//    // Wysylanie wiadomosci do wszystkich, oprocz podanego adresu MAC. Tylko dla serwera, klient wysyła wiadomosc do wszytkich
//   sendStringMessageExeptSpecifiedAddress(String addressMac, String message);
//
//    //Liczba podlaczonych SAMYCH klientow do serwera. Tylko dla serwera
//   getmNbrClientConnection();
//
//   //Wysylanie wiadomosci do wszystkich
//    sendStringMessageForAll(String message);
//
//    //Wysyłanie wiadomosci dla podanego adresu MAC. Tylko dla serwera, klient wysyła wiadomosc do wszytkich
//   sendStringMessage(String addressMac, String message);




