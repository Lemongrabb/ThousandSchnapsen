package com.example.thousandschnapsen.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.thousandschnapsen.MainActivity;
import com.example.thousandschnapsen.R;
import com.example.thousandschnapsen.bluetooth.eventBus.ClientConnectionFailEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.ClientConnectionSuccessEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.MessageSyncEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.ServerConnectionFailEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.ServerConnectionSuccessEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public abstract class GameActivity extends AppCompatActivity {

    public boolean gameReady;
    boolean scanDevices = false;
    String deviceName = "";
    String deviceAddress = "";
    String playerNickName = "";
    String serverName = "";
    int clientNumberOfPLayers = 0;
    public static int MAX_NUMBER_OF_CLIENTS = 2; //Maksymalna liczba klient-ów
    public static int MAX_NUMBER_OF_PLAYERS = MAX_NUMBER_OF_CLIENTS +1;
    boolean connected = false;
    public String message = "";

    String UUID = "f520cf2c-6487-11e7-907b";


    private static final String TAG = "GameActivity";


    private static TextView tv_number_of_players_online;
    static AlertDialog dialogAwating;


    BluetoothManager bluetoothManager;  //Klasa do tworzenia połączenia bt i kontroli komunikacji

    //BroadcastReceiver do wyszukiwania urządzeń bt-bluetooth dla serwera
    private BroadcastReceiver mServerBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "mServerBroadcastReceiver: ACTION FOUND.");

            if (BluetoothDevice.ACTION_FOUND.equals(action) || BluetoothDevice.ACTION_NAME_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "mServerBroadcastReceiver: " + device.getName() + ": " + device.getAddress());
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                if(rssi == -32768){
                    Log.d(TAG, "Ghost server, ignoring...");
                }
                else bluetoothManager.ifTheDeviceIsForThisGame(device);
            }
        }
    };

///EVENT_BUS
    ////////////////////////////////////////////////////////////

    public abstract void bluetoothMessage(String message);
    public abstract void clientConnectionSuccessEvent();
    public abstract void clientConnectionFailEvent(String serverAddress);
    public abstract void serverConnectionSuccessEvent(String clientAddress);
    public abstract void serverConnectionFailEvent(String clientAddress);

    //Służy do odczytywania wiadomości z bt
    @Subscribe
    public void onEvent (MessageSyncEvent syncMessage){
        Log.d(TAG,syncMessage.getSyncMessage());
        message = syncMessage.getSyncMessage();

        if (gameReady){
            bluetoothMessage(message);
            connected = true;
            if(!(serverName == null)) {
                //Jeśli jestem serwerem to odbieram dane i wysyłam do innych co jeszcze nie dostali

                //Służy do wysyłania wiadomości wszystkim poza podanym adresie mac
                sendStringMessageExeptSpecifiedAddress(syncMessage.getSenderMacAddress(), message);
            }
            message = "";
        }
        //Jeśłi został wysłana wiadomość kontrolna to rozpocznij grę
        if (message.equals("$READY$") && serverName == null && !gameReady) {
            hideDialog();
            toastMsg("Rozpoczynamy grę");
            gameReady = true;
        }
        if (message.equals("$!READY$") && serverName == null && gameReady) {
            changeText(2);
            toastMsg("Liczba graczy: 2/" +MAX_NUMBER_OF_PLAYERS);
            gameReady = false;
        }
        if (message.startsWith("$NUM_PLAYERS$")){
            String[] numPlayers = message.split("$NUM_PLAYERS$");
            changeText(Integer.parseInt(numPlayers[1]));
            toastMsg("Liczba graczy: "+numPlayers[1]+"/" +MAX_NUMBER_OF_PLAYERS);

        }

    }

    //Jeśli udało się połączyć się z serwerm
    @Subscribe
    public void onEvent (ClientConnectionSuccessEvent event){
        bluetoothManager.onClientConnectionSuccess();
        Log.d("ClientConnection", "Success");
    }

    //Jeśli utracono lub nie udało się połączyć z serwerem
    @Subscribe
    public void onEvent (ClientConnectionFailEvent serverAddress){
        Log.e("ClientConnectionFail", "Can't connect with server: " + serverAddress.getServerAddress());
        toastMsg("Nie można się połączyć z serwerem lub serwer nie istnieje");
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Jesli doszło pomyślnego połączenia się z klientem
    @Subscribe
    public void onEvent (ServerConnectionSuccessEvent clientAddress){
        bluetoothManager.onServerConnectionSuccess(clientAddress.getClientAddress());
        Log.d("ServerConnection", "Success");
        Log.d("NumberOfPlayers: ", ""+getmNbrClientConnection());

        changeText(getmNbrClientConnection());
        //Jeśli ilość urządzeń == wartości maksymlanej podłączonych urządzeń
        if(getmNbrClientConnection() == MAX_NUMBER_OF_CLIENTS){
            gameReady = true;
            scanDevices = false;    //Przerwij skanowanie urządzeń bt dla serwera
            bluetoothManager.cancelDiscoveryTimer();
            sendStringMessageForAll("$READY$");  //Wyślij znak specjalny do innych urządzeń, że wszystkie urządzenia są gotowe
            hideDialog();
            toastMsg("Rozpoczynamy grę");

            //bluetoothManager.listOfConnectedPlayers tablia z adresami MAC i nick-ami graczy
//            for (String i : bluetoothManager.listOfConnectedPlayers.keySet()) {
//                System.out.println("key: " + i + " value: " + bluetoothManager.listOfConnectedPlayers.get(i));
//            }
        }
        else {
            int numberOfPlayers = getmNbrClientConnection() + 1;
            changeText(numberOfPlayers);
            toastMsg("Liczba graczy: " + numberOfPlayers + " / " + MAX_NUMBER_OF_PLAYERS);
            sendStringMessageForAll("$NUM_PLAYERS$"+numberOfPlayers);
        }
    }

    //Jeśli doszło do przerwania z klientem to tutaj wyrzuca jego adres MAC
    @Subscribe
    public void onEvent (ServerConnectionFailEvent clientAddress){
        bluetoothManager.onServerConnectionFailed(clientAddress.getClientAddress());
        int numberOfPlayers = getmNbrClientConnection() +1;
        Log.d("ServerConnection: ","Device: " + clientAddress.getClientAddress() + " disconnected");
        if (!gameReady){
            changeText(numberOfPlayers);
            toastMsg("Liczba graczy: " + numberOfPlayers + "/" + MAX_NUMBER_OF_PLAYERS);
        }
        if(gameReady){
            gameReady = false;
            Log.d("ServerConnection: ","Device: " + clientAddress.getClientAddress() + " disconnected");
            changeText(numberOfPlayers);
            toastMsg("Liczba graczy: "+numberOfPlayers+"/" +MAX_NUMBER_OF_PLAYERS);
            sendStringMessageForAll("$NUM_PLAYERS$"+numberOfPlayers);
            bluetoothManager.startDiscoveryforServer();
            scanDevices = true;
        }

    }

    ///////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_internet);
        bluetoothManager = new BluetoothManager(GameActivity.this);
        bluetoothManager.setUUIDappIdentifier(UUID);

        //Pobieranie danych z intent PlayBluetoothActivity
        deviceName = getIntent().getStringExtra("DEVICE_NAME");
        deviceAddress = getIntent().getStringExtra("DEVICE_ADDRESS");
        playerNickName = getIntent().getStringExtra("PLAYER_NICK_NAME");  //Nazwa gracza, nazwa admina serwera
        serverName = getIntent().getStringExtra("SERVER_NAME");
        clientNumberOfPLayers = getIntent().getIntExtra("CLIENT_NUMBER_OF_PLAYERS",0);
        Log.d(TAG, "DEVICE_NAME: " + deviceName + " DEVICE_ADDRESS: " + deviceAddress +
                " PLAYER_NICK_NAME: " + playerNickName);

        //Jeśli nazwa serwera nie jest pusta
        if (!(serverName == null)) {
            Log.d(TAG, "START_SERVER name: " + serverName);
            bluetoothManager.setServerName(serverName);
            bluetoothManager.setPlayerAdminName(playerNickName);
            bluetoothManager.setNbrClientMax(MAX_NUMBER_OF_CLIENTS);
            bluetoothManager.setTimeDiscoverable(BluetoothManager.BLUETOOTH_TIME_DICOVERY_600_SEC);
            bluetoothManager.selectServerMode();
            bluetoothManager.startDiscoveryforServer();
            scanDevices = true;
        }

        if (serverName == null) {
            bluetoothManager.selectClientMode();
            Log.d(TAG, "START_CLIENT");
            bluetoothManager.selectClientMode();
            bluetoothManager.createClient(deviceAddress);
        }
        alterDialogAwating();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (scanDevices) {
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
        scanDevices = false;           //Przestań skanować urządzenia
        bluetoothManager.setOldBTDeviceName(); //Ustaw nomalną nazwę urządzenia bt
        bluetoothManager.closeAllConnexion(); //Zamknij wszystkie połączenia bt
        bluetoothManager.cancelDiscoveryTimer(); //Przestań skanować
        if (mServerBroadcastReceiver!=null) { //wyrejestruj BroadcastReciever
            try{
                unregisterReceiver(mServerBroadcastReceiver);
                mServerBroadcastReceiver = null;
            }
            catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }
    }


    //Jeśli wcisnę na telefonie przycisk wstecz
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
        }            Intent intent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Uruchom Broadcast Reciever
    public void scanBluetoothDeviceReciever(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        Log.i(TAG, "Disccovering devices");
        registerReceiver(mServerBroadcastReceiver, filter);
    }


    //Służy do wysyłania dymków Toast
    public void toastMsg(String msg) {
        final String str = msg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GameActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void alterDialogAwating(){
        final LayoutInflater inflater = LayoutInflater.from(GameActivity.this);
        final View view = inflater.inflate(R.layout.awaiting_for_players_dialog, null, false);
        tv_number_of_players_online = view.findViewById(R.id.tv_number_of_players_online);
        final Button button_exit_server = view.findViewById(R.id.button_exit_server);

        button_exit_server.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(GameActivity.this, MainActivity.class));
                finish();
            }
        });


        if (bluetoothManager.typeBluetooth == BluetoothManager.TypeBluetooth.Client){
            clientNumberOfPLayers++;
            tv_number_of_players_online.setText("Liczba graczy: " + clientNumberOfPLayers + " / " + MAX_NUMBER_OF_PLAYERS);
            toastMsg("Liczba graczy: " + clientNumberOfPLayers + " / " + MAX_NUMBER_OF_PLAYERS);
        }
        else if (bluetoothManager.typeBluetooth == BluetoothManager.TypeBluetooth.Server){
            tv_number_of_players_online.setText("Liczba graczy: " + 1 + " / " + MAX_NUMBER_OF_PLAYERS);
        }

        dialogAwating = new AlertDialog.Builder(this)
                .setTitle("Oczekiwanie na graczy...")
                .setView(view)
                .create();
        dialogAwating.setCancelable(false);
        dialogAwating.setCanceledOnTouchOutside(false);
        dialogAwating.show();
    }
    //Zmień wyświetlany tekst o oczekujących graczach w alterDialog
    public static void changeText(final int onlinePlayers) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                dialogAwating.dismiss();
                tv_number_of_players_online.setText("Liczba graczy: " + onlinePlayers + " / " + MAX_NUMBER_OF_PLAYERS);
                dialogAwating.show();

            }
        });
    }
    //Schowaj alterDialog
    public void hideDialog(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                dialogAwating.dismiss();
            }
        });
    }


    public void sendStringMessageExeptSpecifiedAddress(String addressMac, String message){
        bluetoothManager.sendStringMessageExeptSpecifiedAddress(addressMac, message);
    }

    public int getmNbrClientConnection(){
        return bluetoothManager.getmNbrClientConnection();
    }

    public void sendStringMessageForAll(String message){
        bluetoothManager.sendStringMessageForAll(message);
    }

    public void sendStringMessage(String addressMac, String message){
        bluetoothManager.sendStringMessage(addressMac, message);
    }





}
