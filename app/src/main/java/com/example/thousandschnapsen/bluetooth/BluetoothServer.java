package com.example.thousandschnapsen.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.util.Log;

import com.example.thousandschnapsen.bluetooth.eventBus.ServerConnectionFailEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.ServerConnectionSuccessEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.UUID;

//import com.ramimartin.multibluetooth.bus.ServeurConnectionFail;
//import com.ramimartin.multibluetooth.bus.ServeurConnectionSuccess;
//import com.example.thousandschnapsen.bluetooth.eventBus.ServeurConnectionFail;
//import com.example.thousandschnapsen.bluetooth.eventBus.ServeurConnectionSuccess;
//
//import org.greenrobot.eventbus.EventBus;


/**
 * Created by Rami on 16/06/2017.
 */
public class BluetoothServer extends BluetoothRunnable {

    private static final String TAG = BluetoothServer.class.getSimpleName();

    private UUID mUUID;
    private BluetoothServerSocket mServerSocket;

    public BluetoothServer(BluetoothAdapter bluetoothAdapter, String uuiDappIdentifier, String adressMacClient, Activity activity, BluetoothManager.MessageMode messageMode) {
        super(bluetoothAdapter, uuiDappIdentifier, activity, messageMode);
        mClientAddress = adressMacClient;
        mUUID = UUID.fromString(uuiDappIdentifier + "-" + mClientAddress.replace(":", ""));
    }

    @Override
    public void waitForConnection() {
        // NOTHING TO DO IN THE SERVER
    }

    @Override
    public void intiObjReader() throws IOException {
        mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("BLTServer", mUUID);
        mSocket = mServerSocket.accept();
        mInputStream = mSocket.getInputStream();
    }

    @Override
    public void onConnectionSuccess() {
        EventBus.getDefault().post(new ServerConnectionSuccessEvent(mClientAddress));
    }

    @Override
    public void onConnectionFail() {
        EventBus.getDefault().post(new ServerConnectionFailEvent(mClientAddress));
    }

    @Override
    public void closeConnection() {
        super.closeConnection();
        try {
            mServerSocket.close();
            mServerSocket = null;
        } catch (Exception e) {
            Log.e("", "===+++> closeConnection Exception e : "+e.getMessage());
        }
    }
}
