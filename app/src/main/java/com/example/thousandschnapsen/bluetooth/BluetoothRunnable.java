package com.example.thousandschnapsen.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.thousandschnapsen.bluetooth.eventBus.MessageSyncEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;

//import com.example.thousandschnapsen.bluetooth.eventBus.BluetoothCommunicatorBytes;
//import com.example.thousandschnapsen.bluetooth.eventBus.BluetoothCommunicatorObject;
//import com.example.thousandschnapsen.bluetooth.eventBus.BluetoothCommunicatorString;
//
//import org.greenrobot.eventbus.EventBus;


/**
 * Created by Rami on 16/06/2017.
 */
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
    private OutputStreamWriter mOutputStreamWriter;
    private ObjectOutputStream mObjectOutputStream;
    private ObjectInputStream mObjectInputStream;
    private BluetoothManager.MessageMode mMessageMode;
    private int mCountObjectInputStreamExection;
    private boolean mIsConnected;
    EventBus mEventBus = EventBus.getDefault();

    public BluetoothRunnable(BluetoothAdapter bluetoothAdapter, String uuiDappIdentifier, Activity activity, BluetoothManager.MessageMode messageMode) {
        mBluetoothAdapter = bluetoothAdapter;
        mUuiDappIdentifier = uuiDappIdentifier;
        mMessageMode = messageMode;
        mMyAdressMac = bluetoothAdapter.getAddress();
        if (mMyAdressMac.equals("02:00:00:00:00:00")) {
            mMyAdressMac = android.provider.Settings.Secure.getString(activity.getContentResolver(), "bluetooth_address");
        }
        mIsConnected = false;
        mCountObjectInputStreamExection = 0;
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
            mOutputStreamWriter = new OutputStreamWriter(mSocket.getOutputStream());
            if(mSocket == null) return;
            mObjectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());

            mOutputStreamWriter.flush();
            mObjectOutputStream.reset();

            onConnectionSuccess();

            // I DONT KNOW WHY BUT ALWAYS THE FIRST MESSAGE SENT HAS UNWANTED CHARACTERS OR CHARACTERS MISSING
            // SO FOR CLEANING IT I SEND MESSAGE ON THE CONNECTION
//            writeString("Connected");

            while (CONTINUE_READ_WRITE) {

                synchronized (this) {

                    switch (mMessageMode){

                        case Serialized:

                            try {
                                if(mInputStream == null) return;
                                mObjectInputStream = new ObjectInputStream(mInputStream);
                                Object messageObj = mObjectInputStream.readUnshared();    // read from the object stream,
//                                EventBus.getDefault().post(new BluetoothCommunicatorObject(messageObj)); //TODO
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
                                lifeline();
                                if(mIsConnected && null != e.getMessage() && e.getMessage().contains("bt socket closed") && mIsConnected){
                                    onConnectionFail();
                                    mIsConnected = false;
                                }
                            }

                            break;

                        case String:

                            try {
                                final StringBuilder sb = new StringBuilder();
                                if(mInputStream == null) return;
                                bytesRead = mInputStream.read(buffer);
                                if (bytesRead != -1) {
                                    String result = "";
                                    while ((bytesRead == bufferSize) && (buffer[bufferSize] != 0)) {
                                        result = result + new String(buffer, 0, bytesRead);
                                        if(mInputStream == null) return;
                                        bytesRead = mInputStream.read(buffer);
                                    }
                                    result = result + new String(buffer, 0, bytesRead);
                                    sb.append(result);
                                }
                                EventBus.getDefault().post(new MessageSyncEvent(sb.toString(),mClientAddress));
                            } catch (IOException e) {
                                Log.e(TAG, "===> Error Received String IOException : " + e.getMessage());
                                onConnectionFail();
                            }

                            break;

                        case Bytes:
                            try {
                                if(mInputStream == null) return;
                                bytesRead = mInputStream.read(buffer);
                                ByteBuffer bbuf = ByteBuffer.allocate(bytesRead);
                                if (bytesRead != -1) {
                                    while ((bytesRead == bufferSize) && (buffer[bufferSize] != 0)) {
                                        bbuf.put(buffer, 0, bytesRead);
                                        if(mInputStream == null) return;
                                        bytesRead = mInputStream.read(buffer);
                                    }
                                    bbuf.put(buffer, 0, bytesRead);
                                }
//                                EventBus.getDefault().post(new BluetoothCommunicatorBytes(bbuf.array())); //TODO
                            } catch (IOException e) {
                                Log.e(TAG, "===> Error Received Bytes IOException  : " + e.getMessage());
                            }

                            break;
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

    public void lifeline(){
        mCountObjectInputStreamExection++;
        if(mCountObjectInputStreamExection>100){
            CONTINUE_READ_WRITE = false;
            this.closeConnection();
        }
    }

    public abstract void waitForConnection();

    public abstract void intiObjReader() throws IOException;

    public abstract void onConnectionSuccess();

    public abstract void onConnectionFail();

    public void writeString(String message) {
        try {
            if (mOutputStreamWriter != null) {
                mOutputStreamWriter.write(message);
                mOutputStreamWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeBytes(byte[] message) {
        try {
            if (mSocket != null) {
                mSocket.getOutputStream().write(message);
                mSocket.getOutputStream().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeSerialized(Object obj) {
        try {
            if (mObjectOutputStream != null) {
                mObjectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
                mObjectOutputStream.writeUnshared(obj);
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
