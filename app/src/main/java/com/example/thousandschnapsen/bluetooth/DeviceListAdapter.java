package com.example.thousandschnapsen.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.thousandschnapsen.GameBluetoothActivity;
import com.example.thousandschnapsen.GameInternetActivity;
import com.example.thousandschnapsen.MainActivity;
import com.example.thousandschnapsen.PlayBluetoothActivity;
import com.example.thousandschnapsen.PlayInternetActivity;
import com.example.thousandschnapsen.R;

import java.util.ArrayList;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<BluetoothDevice> mDevices;
    BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    private int  mViewResourceId;
    private String playerNickName;
    private static final String TAG = "DeviceListAdapter";


    public DeviceListAdapter(Context context, int tvResourceId, ArrayList<BluetoothDevice> devices, BluetoothAdapter BluetoothAdapter, String playerNickName){
        super(context, tvResourceId,devices);
        this.mDevices = devices;
        this.mContext = context;
        this.mBluetoothAdapter = BluetoothAdapter;
        this.playerNickName = playerNickName;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        final BluetoothDevice device = mDevices.get(position);

        if (device != null) {
            TextView deviceName = (TextView) convertView.findViewById(R.id.tv_server_name);
            TextView deviceAdress = (TextView) convertView.findViewById(R.id.tv_server_mac);
            if (deviceName != null & deviceAdress != null) {
                deviceName.setText(device.getName());
                deviceAdress.setText(device.getAddress());
                Button joinServerButton = (Button) convertView.findViewById(R.id.button_join_server);

                joinServerButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onItemClick: You Clicked on a device.");
                        String deviceName = device.getName();
                        String deviceAddress = device.getAddress();
                        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
                        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

                        mBluetoothAdapter.cancelDiscovery();
                        mBluetoothAdapter.getBondedDevices();

                        Intent intent = new Intent(mContext, GameBluetoothActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("DEVICE_NAME", device.getName());
                        intent.putExtra("DEVICE_ADDRESS", device.getAddress());
                        intent.putExtra("BT_DEVICE", device);
                        intent.putExtra("PLAYER_NICK_NAME", playerNickName);
                        mContext.startActivity(intent);


                    }
                });
            }
        }
        return convertView;
    }
}
