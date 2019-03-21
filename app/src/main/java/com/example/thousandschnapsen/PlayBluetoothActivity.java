package com.example.thousandschnapsen;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ListView;
import com.example.thousandschnapsen.bluetooth.DeviceListAdapter;
import java.util.ArrayList;

public class PlayBluetoothActivity extends Activity {

    private static final String TAG = "PlayBluetoothActivity";
    private static final int COARSE_LOCATION_CODE = 1;
    private static final int DISCOVERABLE_DURATION = 300;

    BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    ListView lvNewDevices;

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                DeviceListAdapter mDeviceListAdapter = new DeviceListAdapter(context, R.layout.row_list_view_bluetooth_servers_list, mBTDevices);
               lvNewDevices.setAdapter(mDeviceListAdapter);
            }

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                final int bond = mDevice.getBondState();
                //3 cases:
                //case1: bonded already
                switch (bond) {
                    case BluetoothDevice.BOND_BONDED:
                        Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                        //inside BroadcastReceiver4
//                    mBTDevice = mDevice;
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                        break;

                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_bluetooth);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            requestLocationCorasePermission();
        }
        else {
            //NOTHING
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBT();
        enableDiscoverability();

        enableDiscoverDevice();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mBroadcastReceiver);
    }




    private void requestLocationCorasePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Ta aplikacja wymaga dostępu do lokalizacji")
                    .setMessage("Udziel dostępu do lokalizacji, aby ta aplikacja mogła wykrywać urządzenia")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(PlayBluetoothActivity.this,
                                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_LOCATION_CODE);
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_LOCATION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == COARSE_LOCATION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG,"COARSE_LOCATION PERMISSION_GRANTED");

            } else {
                Log.d(TAG,"COARSE_LOCATION PERMISSION_DENIED");
                startActivity(new Intent(PlayBluetoothActivity .this, MainActivity.class));
            }
        }
    }

    public void enableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);


//            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//          registerReceiver(mBroadcastReceiver, BTIntent);
        }
    }

    public void enableDiscoverability() {
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
        startActivity(discoverableIntent);

    }

    private void enableDiscoverDevice() {
        if(mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            Log.i(TAG, "Disccovering devices");
            registerReceiver(mBroadcastReceiver, filter);

        }

    }
    }



