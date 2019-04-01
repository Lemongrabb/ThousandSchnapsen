package com.example.thousandschnapsen;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class GameBluetoothActivity extends AppCompatActivity {

    private boolean gameReady;
    boolean awaitingPlayers = false;
    boolean scanDevices = false;
    String deviceName = "";
    String deviceAddress = "";
    String playerNickName = "";
    String serverName = "";
    String senderMacAddress = "";
    public static int NUMBER_OF_PLAYERS = 2;
    public int numberOfConnectedPlayers = 0;
    boolean connected = false;
    private String typeBluetooth = "";
    public String message = "";
    String UUID = "f520cf2c-6487-11e7-907b";


    private static final String TAG = "GameBluetoothActivity";

    Button sendMessage;
    EditText messageText;
    AlertDialog.Builder dialog;

    BluetoothManager bluetoothManager;


    private BroadcastReceiver mServerBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "mServerBroadcastReceiver: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "mServerBroadcastReceiver: " + device.getName() + ": " + device.getAddress());
                bluetoothManager.ifTheDeviceIsForThisGame(device);
            }
        }
    };



    @Subscribe
    public void onEvent (MessageSyncEvent syncMessage){
        Log.d(TAG,syncMessage.getSyncMessage());
        message = syncMessage.getSyncMessage();

        if (gameReady == true){
            toastMsg(message);
            connected = true;
            if(!(serverName == null)) {
                bluetoothManager.sendStringMessageExeptSpecifiedAddress(syncMessage.getSenderMacAddress(), message);
            }
            message = "";
        }

    }


    @Subscribe
    public void onEvent (ClientConnectionSuccessEvent event){
        bluetoothManager.onClientConnectionSuccess();
        gameReady = true;
        Log.d("ClientConnection", "Success");
    }

    @Subscribe
    public void onEvent (ClientConnectionFailEvent serverAddress){
        Log.e("ClientConnectionFail", "Can't connect with server: " + serverAddress.getServerAddress());
        Log.d("ClientConnection", "Failed");
        toastMsg("Nie można się połączyć z serwerem lub serwer nie istnieje");
        Intent intent = new Intent(GameBluetoothActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Subscribe
    public void onEvent (ServerConnectionSuccessEvent clientAddress){
        bluetoothManager.onServerConnectionSuccess(clientAddress.getClientAddress());
        Log.d("ServerConnection", "Success");

        if(bluetoothManager.getmNbrClientConnection()+1 == NUMBER_OF_PLAYERS){
            gameReady = true;
            scanDevices = false;
            bluetoothManager.cancelDiscoveryTimer();
//            mUpdater.removeCallbacksAndMessages(null);
            for (String i : bluetoothManager.listOfConnectedPlayers.keySet()) {
                System.out.println("key: " + i + " value: " + bluetoothManager.listOfConnectedPlayers.get(i));
            }

        }
    }

    @Subscribe
    public void onEvent (ServerConnectionFailEvent clientAddress){
        bluetoothManager.onServerConnectionFailed(clientAddress.getClientAddress());
        Log.d("ServerConnection: ","Device: " + clientAddress.getClientAddress() + " disconnected");



    }

    @Subscribe
    public void onEvent (HowManyPlayersEvent numberOfPlayersEvent){
        numberOfConnectedPlayers = numberOfPlayersEvent.getNumberOfPlayersEvent();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_internet);
        sendMessage = findViewById(R.id.button);
        messageText = findViewById(R.id.editText);


        bluetoothManager = new BluetoothManager(GameBluetoothActivity.this);
        bluetoothManager.setUUIDappIdentifier(UUID);


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothManager.sendStringMessageForAll(messageText.getText().toString());
                Log.d("Send message:",  messageText.getText().toString());
                messageText.getText().clear();
            }
        });

        deviceName = getIntent().getStringExtra("DEVICE_NAME");
        deviceAddress = getIntent().getStringExtra("DEVICE_ADDRESS");
        playerNickName = getIntent().getStringExtra("PLAYER_NICK_NAME");
        serverName = getIntent().getStringExtra("SERVER_NAME");
        Log.d(TAG, "DEVICE_NAME: " + deviceName + " DEVICE_ADDRESS: " + deviceAddress +
                " PLAYER_NICK_NAME: " + playerNickName);

        if (!(serverName == null)) {
//            start server
            Log.d(TAG, "START_SERVER name: " + serverName);
            typeBluetooth = "SERVER";
            bluetoothManager.setServerName(serverName);
            bluetoothManager.setPlayerAdminName(playerNickName);
            bluetoothManager.setNbrClientMax(NUMBER_OF_PLAYERS);
            bluetoothManager.setTimeDiscoverable(BluetoothManager.BLUETOOTH_TIME_DICOVERY_600_SEC);
            bluetoothManager.selectServerMode();
            bluetoothManager.startDiscoveryforServer();
            scanDevices = true;

        }

        if (serverName == null) {
//            start client
            typeBluetooth = "CLIENT";
            Log.d(TAG, "START_CLIENT");
            bluetoothManager.selectClientMode();
            bluetoothManager.createClient(deviceAddress);
        }



        //        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        mUpdateView.run();
//        waitingForGameAlter();
    }


    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (scanDevices == true) {
            scanBluetoothDeviceReciever();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanDevices = false;
        bluetoothManager.setOldBTDeviceName();
        bluetoothManager.closeAllConnexion();
        bluetoothManager.cancelDiscoveryTimer();
        if (mServerBroadcastReceiver!=null) {
            try{
                unregisterReceiver(mServerBroadcastReceiver);
                mServerBroadcastReceiver = null;
            }
            catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        scanDevices = false;
        if(serverName == null){
            bluetoothManager.disconnectClient();
        }
        else bluetoothManager.disconnectServer();
        bluetoothManager.setOldBTDeviceName();
        if (bluetoothManager.mBluetoothAdapter.isEnabled())bluetoothManager.mBluetoothAdapter.disable();
        if (mServerBroadcastReceiver!=null) {
            try{
                unregisterReceiver(mServerBroadcastReceiver);
                mServerBroadcastReceiver = null;
            }
            catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }            Intent intent = new Intent(GameBluetoothActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void scanBluetoothDeviceReciever(){
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            Log.i(TAG, "Disccovering devices");
            registerReceiver(mServerBroadcastReceiver, filter);
    }



//    Handler mUpdater = new Handler();
//    Runnable mUpdateView = new Runnable() {
//        @Override
//        public void run() {
//           waitingForGameAlter();
////            mUpdater.postDelayed(this,5000);
//        }
//    };


//    public void waitingForGameAlter(){
//        final LayoutInflater inflater = LayoutInflater.from(GameBluetoothActivity.this);
//        final View view = inflater.inflate(R.layout.awaiting_for_players_dialog, null, false);
//        final TextView tv_number_of_players_online = view.findViewById(R.id.tv_number_of_players_online);
//        final Button button_exit_server = view.findViewById(R.id.button_exit_server);
//
//        button_exit_server.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                startActivity(new Intent(GameBluetoothActivity.this, MainActivity.class));
//                finish();
//            }
//        });
//
//        dialog = new AlertDialog.Builder(GameBluetoothActivity.this);
//                dialog.setTitle("Oczekiwanie na graczy...");
//                dialog.setView(view);
//                dialog.create();
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
////        int numberOfPlayers = bluetoothManager.getmNbrClientConnection() +1;
////
////
////            tv_number_of_players_online.setText("Liczba graczy: " + numberOfPlayers + " / " + NUMBER_OF_PLAYERS);
////            tv_number_of_players_online.invalidate();
//
//    }

    public void toastMsg(String msg) {
        final String str = msg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GameBluetoothActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }





}


