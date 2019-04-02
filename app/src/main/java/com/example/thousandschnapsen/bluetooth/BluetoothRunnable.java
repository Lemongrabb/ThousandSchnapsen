package com.example.thousandschnapsen.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.thousandschnapsen.bluetooth.eventBus.MessageSyncEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BluetoothRunnable implements Runnable {

    private static final String TAG = BluetoothRunnable.class.getSimpleName();

    public boolean CONTINUE_READ_WRITE = true;

    public String mUuiDappIdentifier;
    public BluetoothAdapter mBluetoothAdapter;
    public BluetoothSocket mSocket;
    public InputStream mInputStream;
    public String mClientAddress;
    public String mServerAddress;
    public String mMyAdressMac;
    private DataOutputStream mOutputStreamWriter;
    private ObjectOutputStream mObjectOutputStream;
    private ObjectInputStream mObjectInputStream;
    private boolean mIsConnected;

    public BluetoothRunnable(BluetoothAdapter bluetoothAdapter, String uuiDappIdentifier, AppCompatActivity activity) {
        mBluetoothAdapter = bluetoothAdapter;
        mUuiDappIdentifier = uuiDappIdentifier;
        mMyAdressMac = bluetoothAdapter.getAddress();
        if (mMyAdressMac.equals("02:00:00:00:00:00")) {
            mMyAdressMac = android.provider.Settings.Secure.getString(activity.getContentResolver(), "bluetooth_address");
        }
        mIsConnected = false;
    }

    @Override
    public void run() {

        waitForConnection();

        try {
            intiObjReader();

            mIsConnected = true;
            int bufferSize = 1024;
            int bytesRead = -1;
            byte[] buffer = new byte[bufferSize];

            if(mSocket == null) return;
            DataOutputStream mOutputStreamWriter = new DataOutputStream(mSocket.getOutputStream());
            if(mSocket == null) return;
            mObjectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());

            mOutputStreamWriter.flush();
            mObjectOutputStream.reset();

            onConnectionSuccess();
            writeString("Connected");

            while (CONTINUE_READ_WRITE) {

                synchronized (this) {
//                    try {


                        try {
                            if(mInputStream == null) return;
                            mObjectInputStream = new ObjectInputStream(mInputStream);
                            String message = (String) mObjectInputStream.readObject();
                        EventBus.getDefault().post(new MessageSyncEvent(message,mClientAddress));
                            if(mInputStream == null) return;
                            bytesRead = mInputStream.read(buffer);
                            if (bytesRead != -1) {
                                while ((bytesRead == bufferSize) && (buffer[bufferSize] != 0)) {
                                    bytesRead = mInputStream.read(buffer);
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            Log.e(TAG, "===> Error Received ObjectInputStream ClassNotFoundException : " + e.getLocalizedMessage());
                        } catch (IOException e) {
                            Log.e(TAG, "===> Error Received ObjectInputStream IOException : " + e.getMessage());
                            if(mIsConnected && null != e.getMessage() && e.getMessage().contains("bt socket closed") && mIsConnected){
                                onConnectionFail();
                                mIsConnected = false;
                            }
                        }

                }
            }

        } catch (IOException e) {
            Log.e("", "===> ERROR thread bluetooth : " + e.getMessage());
            e.printStackTrace();
            if (mIsConnected) {
                onConnectionFail();
            }
            mIsConnected = false;
        }
    }


    public abstract void waitForConnection();

    public abstract void intiObjReader() throws IOException;

    public abstract void onConnectionSuccess();

    public abstract void onConnectionFail();


    public void writeString(String message) {
        try {
            if (mObjectOutputStream != null) {
                mObjectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
                mObjectOutputStream.writeUnshared(message);
                mObjectOutputStream.reset();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error ObjectOutputStream: " + e.getMessage());
        }
    }


    public String getClientAddress() {
        return mClientAddress;
    }

    public void closeConnection() {
        if (mSocket != null) {
            try {
                CONTINUE_READ_WRITE = false;
                mInputStream.close();
                mInputStream = null;
                mOutputStreamWriter.close();
                mOutputStreamWriter = null;
                mObjectOutputStream.close();
                mObjectOutputStream = null;
                mObjectInputStream.close();
                mObjectInputStream = null;
                mSocket.close();
                mSocket = null;
                mIsConnected = false;
            } catch (Exception e) {
                Log.e("", "===+++> closeConnection Exception e : "+e.getMessage());
            }
        }
    }

    public boolean isConnected() {
        return mIsConnected;
    }
}
