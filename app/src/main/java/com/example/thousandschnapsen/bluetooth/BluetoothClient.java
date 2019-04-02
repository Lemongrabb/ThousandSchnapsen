package com.example.thousandschnapsen.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.example.thousandschnapsen.bluetooth.eventBus.ClientConnectionFailEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.ClientConnectionSuccessEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;


public class BluetoothClient extends BluetoothRunnable {


    private UUID mUUID;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothConnector mBluetoothConnector;

    private boolean KEEP_TRYING_CONNEXION;


    public BluetoothClient(BluetoothAdapter bluetoothAdapter, String uuiDappIdentifier, String adressMacServer, AppCompatActivity activity) {
        super(bluetoothAdapter, uuiDappIdentifier, (AppCompatActivity) activity);
        mServerAddress = adressMacServer;
        if (mMyAdressMac == null){
            mUUID = UUID.fromString("f520cf2c-6487-11e7-907b-d3cc16e33459");
        }
        else {
            mUUID = UUID.fromString(uuiDappIdentifier + "-" + mMyAdressMac.replace(":", ""));
        }
        KEEP_TRYING_CONNEXION = true;
    }

    @Override
    public void waitForConnection() {

        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mServerAddress);

        while (mInputStream == null && CONTINUE_READ_WRITE && KEEP_TRYING_CONNEXION) {
            mBluetoothConnector = new BluetoothConnector(mBluetoothDevice, false, mBluetoothAdapter, mUUID);

            try {
                mSocket = mBluetoothConnector.connect().getUnderlyingSocket();
                mInputStream = mSocket.getInputStream();
            } catch (IOException e1) {
                Log.e("", "===> mSocket IOException : "+ e1.getMessage());
                EventBus.getDefault().post(new ClientConnectionFailEvent(mServerAddress));
                e1.printStackTrace();
            }
        }

        if (mSocket == null) {
            Log.e("", "===> mSocket IS NULL");
            return;
        }
    }

    @Override
    public void intiObjReader() throws IOException {
    }

    @Override
    public void onConnectionSuccess() {

        EventBus.getDefault().post(new ClientConnectionSuccessEvent());
    }

    @Override
    public void onConnectionFail() {
        EventBus.getDefault().post(new ClientConnectionFailEvent(mServerAddress));
    }

    @Override
    public void closeConnection() {
        KEEP_TRYING_CONNEXION = false;
        super.closeConnection();
    }
}
