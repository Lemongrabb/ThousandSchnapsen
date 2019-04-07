package com.example.thousandschnapsen.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.example.thousandschnapsen.bluetooth.eventBus.HowManyPlayersEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;

public class BluetoothManager {

    public enum TypeBluetooth {
        Client,
        Server,
        None;
    }


    public static final int BLUETOOTH_TIME_DICOVERY_600_SEC = 600;

    private static int BLUETOOTH_NBR_CLIENT_MAX = 7;

    private AppCompatActivity mActivity;
    public BluetoothAdapter mBluetoothAdapter;
    private BluetoothClient mBluetoothClient;
    private ExecutorService mMessageSenderQueue;

    private ArrayList<String> mAdressListServerWaitingConnection;
    private HashMap<String, BluetoothServer> mServeurWaitingConnectionList;
    private ArrayList<BluetoothServer> mServeurConnectedList;
    private HashMap<String, Thread> mServeurThreadList;
    public HashMap<String, String> playerList;
    public HashMap<String, String> listOfConnectedPlayers;
    private SerialExecutor mSerialExecutor;
    private int mNbrClientConnection;
    public TypeBluetooth typeBluetooth;
    private int mTimeDiscoverable;
    public boolean isConnected;
    private boolean mBluetoothIsEnableOnStart;
    private String mBluetoothNameSaved;
    private String mUuiDappIdentifier;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean mIsTimerCanceled;
    private Thread mThreadClient;
    private String serverName;
    private String playerAdminName;
    private String myAddressMac = "";

    public BluetoothManager(AppCompatActivity activity) {
        mActivity = (AppCompatActivity) activity;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothNameSaved = mBluetoothAdapter.getName();
        mBluetoothIsEnableOnStart = mBluetoothAdapter.isEnabled();
        typeBluetooth = TypeBluetooth.None;
        isConnected = false;
        mNbrClientConnection = 0;
        mAdressListServerWaitingConnection = new ArrayList<>();
        mServeurWaitingConnectionList = new HashMap<>();
        mServeurConnectedList = new ArrayList<>();
        mServeurThreadList = new HashMap<>();
        listOfConnectedPlayers = new HashMap<>();
        playerList = new HashMap<>();
        mSerialExecutor = new SerialExecutor(Executors.newSingleThreadExecutor());
    }

    public void setUUIDappIdentifier(String uuiDappIdentifier) {
        mUuiDappIdentifier = uuiDappIdentifier;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setPlayerAdminName(String playerAdminName) {
        this.playerAdminName = playerAdminName;
    }


    public void selectServerMode() {
        typeBluetooth = TypeBluetooth.Server;
        setServerBluetoothName();
    }

    private void setServerBluetoothName() {
        if (mBluetoothAdapter.getName().startsWith("TS") || mBluetoothAdapter.getName().startsWith("TSS")) {
            String deviceName = mBluetoothAdapter.getName();
            String[] oldDeviceName = deviceName.split("\\$");
            mBluetoothAdapter.setName("TSS " + serverName + " " + playerAdminName + " " +
                    mNbrClientConnection + " " + getNbrClientMax() + " $" + oldDeviceName[1]);
        } else {
            mBluetoothAdapter.setName("TSS " + serverName + " " + playerAdminName + " " +
                    mNbrClientConnection + " " + getNbrClientMax() + " $" + mBluetoothAdapter.getName());
        }
    }



    public void setOldBTDeviceName(){
        if (mBluetoothAdapter.getName().startsWith("TS") || mBluetoothAdapter.getName().startsWith("TSS")) {
            String deviceName = mBluetoothAdapter.getName();
            String[] oldDeviceName = deviceName.split("\\$");
            mBluetoothAdapter.setName(oldDeviceName[1]);
        }
    }


    public void selectClientMode() {
        typeBluetooth = TypeBluetooth.Client;
    }

    public void resetMode() {
        typeBluetooth = TypeBluetooth.None;
    }


    public void setNbrClientMax(int nbrClientMax) {
        if (nbrClientMax <= BLUETOOTH_NBR_CLIENT_MAX) {
            BLUETOOTH_NBR_CLIENT_MAX = nbrClientMax;
        }
    }

    public int getNbrClientMax() {
        return BLUETOOTH_NBR_CLIENT_MAX;
    }

    public int getmNbrClientConnection() {
        return mNbrClientConnection;
    }

    public void incrementNbrConnection() {
        mNbrClientConnection++;
        Log.e("", "===> incrementNbrConnection mNbrClientConnection : " + mNbrClientConnection);
        EventBus.getDefault().post(new HowManyPlayersEvent(mNbrClientConnection));
        setServerBluetoothName();
        if (mNbrClientConnection == getNbrClientMax()) {
            Log.e("", "===> incrementNbrConnection mNbrClientConnection OK");
            resetAllOtherWaitingThreadServer();
        }
    }

    public void decrementNbrConnection() {
        if (mNbrClientConnection == 0) {
            return;
        }
        mNbrClientConnection = mNbrClientConnection - 1;
        if (mNbrClientConnection == 0) {
            isConnected = false;
        }
        Log.e("", "===> decrementNbrConnection mNbrClientConnection : " + mNbrClientConnection);
        setServerBluetoothName();
    }

    public void startDiscoveryforServer() {
        mIsTimerCanceled = true;
        if(null == mTimerTask){
            mTimerTask = createTimer();
        }else{
            mTimerTask.run();
        }
        if (mTimer != null) {
            return;
        }
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(mTimerTask, 0, 4000);
    }


    public void cancelDiscovery() {
        mIsTimerCanceled = false;
            mBluetoothAdapter.cancelDiscovery();
            cancelDiscoveryTimer();

    }




    public TimerTask createTimer(){
        return new TimerTask() {

            @Override
            public void run() {
                if (!mIsTimerCanceled) {
                    cancelDiscoveryTimer();
                }
                Log.e("", "===> TimerTask startDiscovery");
                if(mBluetoothAdapter != null){
                    mBluetoothAdapter.startDiscovery();
                }
            }
        };
    }

    public void setTimeDiscoverable(int timeInSec) {
        mTimeDiscoverable = timeInSec;
    }


    public void cancelDiscoveryTimer() {
        if (mTimer == null) {
            return;
        }
        mTimerTask = null;
        mTimer.cancel();
        mTimer = null;
    }

    public BluetoothManager.TypeBluetooth getTypeBluetooth() {
        return typeBluetooth;
    }

    private void resetAllOtherWaitingThreadServer() {
        cancelDiscovery();
        for (Iterator<Map.Entry<String, BluetoothServer>> it = mServeurWaitingConnectionList.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, BluetoothServer> bluetoothServerMap = it.next();
            if (!bluetoothServerMap.getValue().isConnected()) {
                Log.e("", "===> resetWaitingThreadServer BluetoothServer : " + bluetoothServerMap.getKey());
                bluetoothServerMap.getValue().closeConnection();
                Thread serverThread = mServeurThreadList.get(bluetoothServerMap.getKey());
                serverThread.interrupt();
                mServeurThreadList.remove(bluetoothServerMap.getKey());
                it.remove();
            }
        }
    }

    public void createClient(String addressMac) {
        if (typeBluetooth == TypeBluetooth.Client) {
            mBluetoothClient = new BluetoothClient(mBluetoothAdapter, mUuiDappIdentifier, addressMac, mActivity);
            mThreadClient = new Thread(mBluetoothClient);
            mThreadClient.start();
        }
    }

    public void onClientConnectionSuccess(){
        if (typeBluetooth == TypeBluetooth.Client) {
            isConnected = true;
            cancelDiscovery();
        }
    }

    public boolean createServer(String address) {
        if (typeBluetooth == TypeBluetooth.Server && !mAdressListServerWaitingConnection.contains(address)) {
            BluetoothServer mBluetoothServer = new BluetoothServer(mBluetoothAdapter, mUuiDappIdentifier, address, mActivity);
            Thread threadServer = new Thread(mBluetoothServer);
            threadServer.start();
            setServerWaitingConnection(address, mBluetoothServer, threadServer);
//            IntentFilter bondStateIntent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            return true;
        } else {
            return false;
        }
    }

    public void setServerWaitingConnection(String address, BluetoothServer bluetoothServer, Thread threadServer) {
        mAdressListServerWaitingConnection.add(address);
        mServeurWaitingConnectionList.put(address, bluetoothServer);
        mServeurThreadList.put(address, threadServer);
    }

    public void onServerConnectionSuccess(String addressClientConnected) {
        for (Map.Entry<String, BluetoothServer> bluetoothServerMap : mServeurWaitingConnectionList.entrySet()) {
            if (addressClientConnected.equals(bluetoothServerMap.getValue().getClientAddress())) {
                isConnected = true;
                mServeurConnectedList.add(bluetoothServerMap.getValue());
                listOfConnectedPlayers.put(bluetoothServerMap.getValue().mClientAddress,
                        playerList.get(bluetoothServerMap.getValue().mClientAddress));
                Log.e("", "===> onServerConnectionSuccess address : " + addressClientConnected);
                incrementNbrConnection();
                return;
            }
        }
    }

    public void onServerConnectionFailed(String addressClientConnectionFailed) {
        int index = 0;
        for (BluetoothServer bluetoothServer : mServeurConnectedList) {
            if (addressClientConnectionFailed.equals(bluetoothServer.getClientAddress())) {
                mServeurConnectedList.get(index).closeConnection();
                mServeurConnectedList.remove(index);
                mServeurWaitingConnectionList.get(addressClientConnectionFailed).closeConnection();
                mServeurWaitingConnectionList.remove(addressClientConnectionFailed);
                mServeurThreadList.get(addressClientConnectionFailed).interrupt();
                mServeurThreadList.remove(addressClientConnectionFailed);
                mAdressListServerWaitingConnection.remove(addressClientConnectionFailed);
                decrementNbrConnection();
                Log.e("", "===> onServerConnectionFailed address : " + addressClientConnectionFailed);
                return;
            }
            index++;
        }
    }

    public synchronized void sendStringMessageForAll(final String message) {
        Log.e("", "===> sendMessageForAll ");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (typeBluetooth != null && isConnected) {
                    if (mServeurConnectedList != null) {
                        for (BluetoothServer bluetoothServer : mServeurConnectedList) {
                            bluetoothServer.writeString(message);
                        }
                    }
                    if (mBluetoothClient != null) {
                        mBluetoothClient.writeString(message);
                    }
                }
            }
        };
        mSerialExecutor.execute(runnable);
    }

    public void sendStringMessage(final String adressMacTarget, final String message) {
        Log.e("", "===> sendMessage ");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (typeBluetooth != null && isConnected) {
                    if (mServeurConnectedList != null) {
                        for (BluetoothServer bluetoothServer : mServeurConnectedList) {
                            if (bluetoothServer.getClientAddress().equals(adressMacTarget)) {
                                bluetoothServer.writeString(message);
                            }
                        }
                    }
                    if (mBluetoothClient != null) {
                        mBluetoothClient.writeString(message);
                    }
                }
            }
        };
        mSerialExecutor.execute(runnable);
    }

    public void sendStringMessageExeptSpecifiedAddress(final String adressMacTarget, final String message) {
        if(typeBluetooth  == TypeBluetooth.Server) {
            Log.e("", "===> sendMessage ");
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (typeBluetooth != null && isConnected) {
                        if (mServeurConnectedList != null) {
                            for (BluetoothServer bluetoothServer : mServeurConnectedList) {
                                if (!bluetoothServer.getClientAddress().equals(adressMacTarget)) {
                                    bluetoothServer.writeString(message);
                                }
                            }
                        }
                        if (mBluetoothClient != null) {
                            mBluetoothClient.writeString(message);
                        }
                    }
                }
            };
            mSerialExecutor.execute(runnable);
        }
    }



    public void ifTheDeviceIsForThisGame(BluetoothDevice device) {
        if ((device.getName() == null)) {
            Log.d("Gamebluetoothactivity: ", "Device with null name, ignoring...");
        }
         else  if (device.getName().startsWith("TS")) isBluetoothOnListExist(device);

    }

    public void isBluetoothOnListExist(BluetoothDevice device){
            if (!mAdressListServerWaitingConnection.contains(device.getAddress())) {
                Log.d("isBluetoothOnListExist","Creating server thread for" + device.getName() + "  " + device.getAddress());
                String nickName = getNickName(device);
                playerList.put(device.getAddress(), nickName);
                createServer(device.getAddress());
            }
    }

    private String getNickName(BluetoothDevice device){
        if (device.getName().startsWith("TS")) {
            String deviceNickName = device.getName();
            String[] nickname = deviceNickName.split(" ");
            return nickname[1];
        }
        return "";
    }

    public void disconnectClient() {
        typeBluetooth = TypeBluetooth.None;
        resetClient();
    }

    public void disconnectServer() {
        resetMode();
        resetAllThreadServer();
        isConnected = false;
    }

    public void resetClient() {
        if (mBluetoothClient != null) {
            mBluetoothClient.closeConnection();
            if(null != mThreadClient){
                mThreadClient.interrupt();
            }
            mBluetoothClient = null;
            isConnected = false;
        }
    }

    public void resetAllThreadServer(){
        for (Iterator<Map.Entry<String, BluetoothServer>> it = mServeurWaitingConnectionList.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, BluetoothServer> bluetoothServerMap = it.next();
            listOfConnectedPlayers.clear();
            playerList.clear();
            bluetoothServerMap.getValue().closeConnection();
            Thread serverThread = mServeurThreadList.get(bluetoothServerMap.getKey());
            serverThread.interrupt();
            mServeurThreadList.remove(bluetoothServerMap.getKey());
            it.remove();
        }
        mServeurConnectedList.clear();
        mAdressListServerWaitingConnection.clear();
        mServeurWaitingConnectionList.clear();
        mServeurThreadList.clear();
        mNbrClientConnection = 0;
    }

    public void closeAllConnexion() {
        mBluetoothAdapter.setName(mBluetoothNameSaved);

        cancelDiscovery();

        if (typeBluetooth != null) {
            resetAllThreadServer();
            resetClient();
        }

        try {
        } catch (Exception e) {
        }

        if (!mBluetoothIsEnableOnStart) {
            mBluetoothAdapter.disable();
        }

        mBluetoothAdapter = null;
    }
}