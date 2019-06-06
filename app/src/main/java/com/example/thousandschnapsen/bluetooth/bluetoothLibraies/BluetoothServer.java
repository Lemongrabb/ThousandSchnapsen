package com.example.thousandschnapsen.bluetooth.bluetoothLibraies;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.util.Log;

import com.example.thousandschnapsen.bluetooth.bluetoothLibraies.eventBus.ServerConnectionFailEvent;
import com.example.thousandschnapsen.bluetooth.bluetoothLibraies.eventBus.ServerConnectionSuccessEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class BluetoothServer extends BluetoothRunnable {


    private UUID mUUID;
    private BluetoothServerSocket mServerSocket;

    public BluetoothServer(BluetoothAdapter bluetoothAdapter, String uuiDappIdentifier, String adressMacClient, AppCompatActivity activity) {
        super(bluetoothAdapter, uuiDappIdentifier, activity);
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
