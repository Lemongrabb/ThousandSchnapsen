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


    public DeviceListAdapter(Context context, int tvResourceId, ArrayList<BluetoothDevice> devices,
                             BluetoothAdapter BluetoothAdapter, String playerNickName){
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
            TextView deviceName =  convertView.findViewById(R.id.tv_server_name);
            TextView tVNumberOfPlayers = convertView.findViewById(R.id.tv_number_of_players);
            if (deviceName != null) {

                Button joinServerButton =  convertView.findViewById(R.id.button_join_server);

                String[] dividedDeviceName = device.getName().split(" ");
                tVNumberOfPlayers.setText(Integer.parseInt(dividedDeviceName[3])+1 + "/3");
                deviceName.setText(dividedDeviceName[1]);

                joinServerButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onItemClick: You Clicked on a device.");
                        String deviceName = device.getName();
                        String deviceAddress = device.getAddress();

                        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
                        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

                        mBluetoothAdapter.cancelDiscovery();

                        Intent intent = new Intent(mContext, GameBluetoothActivity.class);
                        intent.putExtra("DEVICE_NAME", device.getName());
                        intent.putExtra("DEVICE_ADDRESS", device.getAddress());
                        intent.putExtra("BT_DEVICE", device);
                        intent.putExtra("PLAYER_NICK_NAME", playerNickName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                });
            }
        }
        return convertView;
    }
}
