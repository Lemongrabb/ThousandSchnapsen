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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.thousandschnapsen.bluetooth.DeviceListAdapter;


import java.util.ArrayList;

public class PlayBluetoothActivity extends Activity {

    private static final String TAG = "PlayBluetoothActivity";
    private static final int COARSE_LOCATION_CODE = 1;
    private static final int DISCOVERABLE_DURATION = 300;

    BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    BluetoothDevice mBTDevice;
    Button createServer;
    String playerNickName;

    ListView lvNewDevices;

    int numberOfPlayers = 0;


    private BroadcastReceiver mPlayBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                DeviceListAdapter mDeviceListAdapter = new DeviceListAdapter(context, R.layout.row_list_view_bluetooth_servers_list, mBTDevices, mBluetoothAdapter, playerNickName);
               lvNewDevices.setAdapter(mDeviceListAdapter);
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_bluetooth);
        lvNewDevices = findViewById(R.id.servers_list_view);
        createServer = findViewById(R.id.buttonCreateServer);

        createServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateServerDialog();
            }
        });

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            requestLocationCorasePermission();
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBT();
        showSetNickNameDialog();
        enableDiscoverability();
        enableDiscoverDevice();



    }

    private void showSetNickNameDialog() {
        final EditText et_nickName = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        et_nickName.setLayoutParams(lp);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Graj przez Internet")
                .setMessage("Podaj swój Nick")
                .setView(et_nickName)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playerNickName = et_nickName.getText().toString();
                        if(playerNickName.isEmpty()) {
                            showSetNickNameDialog();
                        }

                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playerNickName = "";
                        startActivity(new Intent(PlayBluetoothActivity.this, MainActivity.class));
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showCreateServerDialog() {
//        serverName = "";
            numberOfPlayers = 0;
            mBluetoothAdapter.cancelDiscovery();
            final LayoutInflater inflater = LayoutInflater.from(this);
            final View view = inflater.inflate(R.layout.create_server_internet_dialog, null, false);

//        final EditText et_server_name = view.findViewById(R.id.et_server_name);
            final RadioGroup radioGroup = view.findViewById(R.id.radioGroup);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(checkedId == R.id.rb_two_players) {
                        numberOfPlayers = 2;
                    } else if(checkedId == R.id.rb_three_players) {
                        numberOfPlayers = 3;
                    } else if(checkedId == R.id.rb_four_players) {
                        numberOfPlayers = 4;
                    }
                }
            });

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Stwórz serwer")
                    .setMessage("Wybierz liczbę graczy i podaj nazwę serwera")
                    .setView(view)
                    .setPositiveButton("Stwórz", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(numberOfPlayers == 2 || numberOfPlayers == 3 || numberOfPlayers == 4) {
                                Intent intent = new Intent(PlayBluetoothActivity.this, GameBluetoothActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("DEVICE_NAME", mBluetoothAdapter.getName());
                                intent.putExtra("DEVICE_ADDRESS", mBluetoothAdapter.getAddress());
                                intent.putExtra("NUMBER_OF_PLAYERS", numberOfPlayers);
                                intent.putExtra("PLAYER_NICK_NAME", playerNickName);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                unregisterReceiver(mPlayBroadcastReceiver);
                                startActivity(intent);
                            } else {
                                showCreateServerDialog();
                                Toast.makeText(getApplicationContext(),"Wypełnij wszystkie pola!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            numberOfPlayers = 0;
//                            serverName = "";
                        }
                    })
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.cancelDiscovery();
        unregisterReceiver(mPlayBroadcastReceiver);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlayBluetoothActivity.this, MainActivity.class);
        startActivity(intent);
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

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mPlayBroadcastReceiver, BTIntent);


        }
    }

    public void enableDiscoverability() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
        startActivity(discoverableIntent);

    }

    private void enableDiscoverDevice() {
        if(mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            Log.i(TAG, "Disccovering devices");
            registerReceiver(mPlayBroadcastReceiver, filter);

        }

    }




}



