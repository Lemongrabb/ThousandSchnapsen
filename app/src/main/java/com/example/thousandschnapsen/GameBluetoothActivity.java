package com.example.thousandschnapsen;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thousandschnapsen.bluetooth.BluetoothManager;
import com.example.thousandschnapsen.bluetooth.eventBus.ClientConnectionFailEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.ClientConnectionSuccessEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.HowManyPlayersEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.MessageSyncEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.ServerConnectionFailEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.ServerConnectionSuccessEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class GameBluetoothActivity extends Activity {



    String deviceName = "";
    String deviceAddress = "";
    String playerNickName = "";
    public int numberOfPlayers = 0;
    public int numberOfConnectedPlayers = 0;
    boolean isClientOnServer = false;
    private String typeBluetooth = "";
    public String message = "";
    String UUID = "f520cf2c-6487-11e7-907b";

//    final Dialog dialog = new Dialog(this);

    private static final String TAG = "GameBluetoothActivity";

    Button sendMessage;
    EditText messageText;

    BluetoothManager bluetoothManager;


    private final BroadcastReceiver mServerBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "mServerBroadcastReceiver: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "mServerBroadcastReceiver: " + device.getName() + ": " + device.getAddress());
                bluetoothManager.isBluetoothOnListExist(device);

            }

        }
    };


    @Subscribe
    public void onEvent (MessageSyncEvent syncMessage){
        Log.d(TAG,syncMessage.getSyncMessage());

        message = syncMessage.getSyncMessage();

        if (isClientOnServer == true) {
            toastMsg(message);
        }
        else {
            message = "";
            isClientOnServer = true;
        }
    }


    @Subscribe
    public void onEvent (ClientConnectionSuccessEvent event){
        bluetoothManager.onClientConnectionSuccess();
    }

    @Subscribe
    public void onEvent (ClientConnectionFailEvent serverAddress){
        Log.e("ClientConnectionFail", "Can't connect with server: " + serverAddress.getServerAddress());
        toastMsg("Nie można się połączyć z serwerem");
        finish();
    }

    @Subscribe
    public void onEvent (ServerConnectionSuccessEvent clientAddress){
        bluetoothManager.onServerConnectionSuccess(clientAddress.getClientAddress());
    }

    @Subscribe
    public void onEvent (ServerConnectionFailEvent clientAddress){
        bluetoothManager.onServerConnectionFailed(clientAddress.getClientAddress());
        toastMsg("Urządzenie:" + clientAddress.getClientAddress() + " odłączyło się");

    }

    @Subscribe
    public void onEvent (HowManyPlayersEvent numberOfPlayersEvent){
        int numberOfConnectedPlayers = numberOfPlayersEvent.getNumberOfPlayersEvent();

        if(numberOfPlayers == numberOfConnectedPlayers){
//            dialog.hide();
        }
//        showServerWaitingStatusDialog(numberOfPlayers, numberOfConnectedPlayers);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_bluetooth);
        sendMessage = findViewById(R.id.button_send);
        messageText = findViewById(R.id.messageText);

        bluetoothManager = new BluetoothManager(this);
        bluetoothManager.setUUIDappIdentifier(UUID);


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothManager.sendStringMessageForAll(messageText.getText().toString());
                Log.d("Send message:",  messageText.getText().toString());
                messageText.setText("");
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
            bluetoothManager.setNbrClientMax(numberOfPlayers);
            bluetoothManager.setTimeDiscoverable(BluetoothManager.BLUETOOTH_TIME_DICOVERY_600_SEC);
            bluetoothManager.selectServerMode();
            bluetoothManager.scanAllBluetoothDevice();
            scanBluetoothDeviceForThreadServer();
        }

        if (numberOfPlayers == 0) {
//            start cliaent
//            dialog.setTitle("Oczekiwanie na serwer");
//            dialog.show();

            typeBluetooth = "CLIENT";
            Log.d(TAG, "START_CLIENT");
            bluetoothManager.selectClientMode();
            bluetoothManager.createClient(deviceAddress);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothManager.closeAllConnexion();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void scanBluetoothDeviceForThreadServer(){
        BluetoothAdapter mBluetoothAdapter;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            Log.i(TAG, "Disccovering devices");
            registerReceiver(mServerBroadcastReceiver, filter);

        }
    }

    public void toastMsg(String msg) {
        final String str = msg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GameBluetoothActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public void showServerWaitingStatusDialog(int numberOfPlayers, int currentNumberOfPlayers) {
//        final int numberOfPlayersDialog = numberOfPlayers;
//        final int currentNumberOfPlayersDialog = currentNumberOfPlayers;
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                dialog.setContentView(R.layout.awaiting_for_players_dialog);
//                dialog.setTitle("Oczekiwanie na graczy...");
//                Button dialogButton = findViewById(R.id.button_exit_server);
//
//                dialogButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        Intent intent = new Intent(GameBluetoothActivity.this, PlayBluetoothActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                });
//                dialog.show();
//            }
//        });
//    }



}


