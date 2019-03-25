package com.example.thousandschnapsen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.thousandschnapsen.bluetooth.BluetoothManager;


public class GameBluetoothActivity extends AppCompatActivity {



    String deviceName = "";
    String deviceAddress = "";
    String playerNickName = "";
    String message = "";
    int numberOfPlayers = 0;
    private String typeBluetooth = "";
    String UUID = "f520cf2c-6487-11e7-907b";

    private static final String TAG = "GameBluetoothActivity";

    Button sendMessage;
    EditText editText;

    BluetoothManager bluetoothManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_bluetooth);
        sendMessage = findViewById(R.id.button);
        editText = findViewById(R.id.editText);

        bluetoothManager = new BluetoothManager(this);
        bluetoothManager.setUUIDappIdentifier(UUID);


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = editText.getText().toString();
                bluetoothManager.sendStringMessageForAll(message);
                editText.setText("");
            }
        });



        deviceName = getIntent().getStringExtra("DEVICE_NAME");
        deviceAddress = getIntent().getStringExtra("DEVICE_ADDRESS");
        playerNickName = getIntent().getStringExtra("PLAYER_NICK_NAME");
        numberOfPlayers = getIntent().getIntExtra("NUMBER_OF_PLAYERS", 0);

        Log.d(TAG, "DEVICE_NAME: " + deviceName + " DEVICE_ADDRESS: " + deviceAddress + " PLAYER_NICK_NAME: " + playerNickName);

        if (numberOfPlayers == 2 || numberOfPlayers == 3 || numberOfPlayers == 4) {
//            start server
            Log.d(TAG, "START_SERVER");
            typeBluetooth = "SERVER";
            bluetoothManager.setNbrClientMax(numberOfPlayers - 1);
            bluetoothManager.setTimeDiscoverable(BluetoothManager.BLUETOOTH_TIME_DICOVERY_600_SEC);
            bluetoothManager.selectServerMode();
            bluetoothManager.scanAllBluetoothDevice();

        }
        if (numberOfPlayers == 0) {
//            start cliaent
            typeBluetooth = "CLIENT";
            Log.d(TAG, "START_CLIENT");
            bluetoothManager.selectClientMode();
            bluetoothManager.createClient(deviceAddress);
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothManager.closeAllConnexion();
    }

}


