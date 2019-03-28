package com.example.thousandschnapsen;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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


public class GameBluetoothActivity extends Activity {


    boolean awaitingPlayers = false;
    String deviceName = "";
    String deviceAddress = "";
    String playerNickName = "";
    String serverName = "";
    String senderMacAddress = "";
    public static int NUMBER_OF_PLAYERS = 3;
    public int numberOfConnectedPlayers = 0;
    boolean connected = false;
    private String typeBluetooth = "";
    public String message = "";
    String UUID = "f520cf2c-6487-11e7-907b";


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
                bluetoothManager.ifTheDeviceIsForthisGame(device);
            }
        }
    };
    private boolean gameReady;


    @Subscribe
    public void onEvent (MessageSyncEvent syncMessage){
        Log.d(TAG,syncMessage.getSyncMessage());
        message = syncMessage.getSyncMessage();

        if (gameReady == true){
            toastMsg(message);
            connected = true;
            if(!(serverName == "")) {
                bluetoothManager.sendStringMessageExeptSpecifiedAddress(syncMessage.getSenderMacAddress()
                        , message);
            }
            message = "";
        }
        if (gameReady == false) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gameReady = true;
                    message = "";



        }

    }


    @Subscribe
    public void onEvent (ClientConnectionSuccessEvent event){
        bluetoothManager.onClientConnectionSuccess();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
    }

    @Subscribe
    public void onEvent (ServerConnectionFailEvent clientAddress){
        bluetoothManager.onServerConnectionFailed(clientAddress.getClientAddress());
        Log.d("ServerConnection: ","Device: " + clientAddress.getClientAddress() + " disconnected");

    }

    @Subscribe
    public void onEvent (HowManyPlayersEvent numberOfPlayersEvent){
        numberOfConnectedPlayers = numberOfPlayersEvent.getNumberOfPlayersEvent();

        if (!((numberOfConnectedPlayers) == NUMBER_OF_PLAYERS)){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_internet);
        sendMessage = findViewById(R.id.button);
        messageText = findViewById(R.id.editText);


        bluetoothManager = new BluetoothManager(this);
        bluetoothManager.setUUIDappIdentifier(UUID);


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothManager.sendStringMessageForAll(messageText.getText().toString());
                Log.d("Send message:",  messageText.getText().toString());
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
            bluetoothManager.scanAllBluetoothDevice();
            scanBluetoothDeviceForThreadServer();
        }

        if (serverName == null) {
//            start client
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
        if(serverName == null){
            bluetoothManager.disconnectClient();
        }
        else bluetoothManager.disconnectServer();
        bluetoothManager.setOldBTDeviceName();
        Intent intent = new Intent(GameBluetoothActivity.this, MainActivity.class);
        startActivity(intent);
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

    public void waitingForGameAlter(){
        final LayoutInflater inflater = LayoutInflater.from(GameBluetoothActivity.this);
        final View view = inflater.inflate(R.layout.awaiting_for_players_dialog, null, false);
        final TextView tv_number_of_players_online = view.findViewById(R.id.tv_number_of_players_online);
        final Button button_exit_server = view.findViewById(R.id.button_exit_server);

        button_exit_server.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(GameBluetoothActivity.this, MainActivity.class));
                finish();
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(GameBluetoothActivity.this)
                .setTitle("Oczekiwanie na graczy...")
                .setView(view)
                .create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        if (serverName == null) {

        }
        else {
            tv_number_of_players_online.setText("Liczba graczy: " + numberOfConnectedPlayers + " / " + NUMBER_OF_PLAYERS);
            if ((numberOfConnectedPlayers) < (NUMBER_OF_PLAYERS)) {
                if (awaitingPlayers == false) {
                    dialog.show();
                    awaitingPlayers = true;
                }
                Toast.makeText(getApplicationContext(), "Liczba graczy: " + numberOfConnectedPlayers + " / " + NUMBER_OF_PLAYERS, Toast.LENGTH_SHORT).show();
            } else {
                if (awaitingPlayers == true) {
                    dialog.dismiss();
                    awaitingPlayers = false;
                }
                Toast.makeText(getApplicationContext(), "Serwer gotowy! Liczba graczy: " + numberOfConnectedPlayers + " / " + NUMBER_OF_PLAYERS, Toast.LENGTH_SHORT).show();
            }
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





}


