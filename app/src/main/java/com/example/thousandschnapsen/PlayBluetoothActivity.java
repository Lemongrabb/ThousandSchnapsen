package com.example.thousandschnapsen;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thousandschnapsen.bluetooth.DeviceListAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PlayBluetoothActivity extends AppCompatActivity {

    private static final String TAG = "PlayBluetoothActivity: ";
    private static final int COARSE_LOCATION_CODE = 1;
    private static final int DISCOVERABLE_DURATION = 600; // widoczność urządzenia 10min

    BluetoothAdapter mBluetoothAdapter;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public ArrayList<BluetoothDevice> mBTDevicesOld = new ArrayList<>();
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean mIsTimerCanceled;
    Button createServer;
    String playerNickName;
    String serverName = "";
    boolean discoverDevice = false;


    ListView lvNewDevices;
//wyszukuje urządzenia bt-bluetooth i wyświetla na liście
    private BroadcastReceiver mPlayBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (BluetoothDevice.ACTION_FOUND.equals(action) || BluetoothDevice.ACTION_NAME_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                if ((device.getName() == null)){
                    Log.d(TAG, "Device with null name, ignoring...");
                }
                if (rssi == -32768){
                    Log.d(TAG, "Ghost server, ignoring...");
                }
                else if (device.getName().startsWith("TSS") && !mBTDevices.contains(device)) {
                    mBTDevices.add(device);
                    Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress()+ "  RSSI: "+ rssi);
                }

                if (!((mBTDevices) == mBTDevicesOld)) {
                    mBTDevicesOld = (ArrayList<BluetoothDevice>) mBTDevices.clone();
                    Log.d("","Changing lv");
                    DeviceListAdapter mDeviceListAdapter = new DeviceListAdapter(context,  R.layout.row_list_view_bluetooth_servers_list, mBTDevices, mBluetoothAdapter, playerNickName);
                    lvNewDevices.setAdapter(mDeviceListAdapter);
                }
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

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) { //Sprawdzenie czy gra musi mieć uprawnienia
            requestLocationCorasePermission();
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!mBluetoothAdapter.isEnabled()){  //Czy bluetooth jest uruchominy, jeśli nie to pokaż informacje o uruchumieniu bt
            showEnableBTDialog();
        }
        else showSetNickNameDialog(); //Jeśli bt jest uruchominy to wpisz nazwę użytkownika

        //skanowanie urządzeń
        enableDiscoverability();
        discoverDevice = true;
        scanAllBluetoothDevice();
    }

    @Override
    protected void onStop() {
        super.onStop();
        discoverDevice = false;
        cancelDiscoveryTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(discoverDevice && !isFinishing()) enableDiscoverDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        discoverDevice = false;
        cancelDiscoveryTimer();
        mBluetoothAdapter.cancelDiscovery();
        if (mPlayBroadcastReceiver!=null) {
            try{
                unregisterReceiver(mPlayBroadcastReceiver);
                mPlayBroadcastReceiver = null;
            }
            catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }
    }

//jeśli naciśniesz wstecz to zamkij okno
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlayBluetoothActivity.this, MainActivity.class);
        startActivity(intent);
        cancelDiscoveryTimer();
        setOldBTDeviceName();
        finish();
    }

//wyświtlenie okna do ustawienia nazwy gracza
    private void showSetNickNameDialog() {
        final EditText et_nickName = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        et_nickName.setLayoutParams(lp);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Graj przez Bluetooth")
                .setMessage("Podaj swój Nick")
                .setView(et_nickName)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playerNickName = et_nickName.getText().toString()
                                .replace(" ","").replace("$","S");
                        setNewBTDeviceName(playerNickName);
                        et_nickName.getText().clear();
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

    //utwórz taką nazwę urządzenia by była widoczna przez serwer
    private void setNewBTDeviceName(String playerNickName) {
        if (mBluetoothAdapter.getName().startsWith("TS") || mBluetoothAdapter.getName().startsWith("TSS")) {
            String deviceName = mBluetoothAdapter.getName();
            String[] oldDeviceName = deviceName.split("\\$");
            mBluetoothAdapter.setName("TS " + playerNickName + " $" + oldDeviceName[1]);
        }
        else {
            mBluetoothAdapter.setName("TS " + playerNickName + " $" + mBluetoothAdapter.getName());
            Log.d("Device Name: ", mBluetoothAdapter.getName());
        }
    }
    //ustaw starą nawę urządzenia jaka była wcześniej
    private void setOldBTDeviceName(){
        if (mBluetoothAdapter.getName().startsWith("TS") || mBluetoothAdapter.getName().startsWith("TSS")) {
            String deviceName = mBluetoothAdapter.getName();
            String[] oldDeviceName = deviceName.split("\\$");
            mBluetoothAdapter.setName(oldDeviceName[1]);
        }
    }

    //okno do tworzenia serwera
    private void showCreateServerDialog() {
        if(!mBluetoothAdapter.isEnabled()){
            showEnableBTDialog();
        }
        else {
            mBluetoothAdapter.cancelDiscovery();
            final LayoutInflater inflater = LayoutInflater.from(this);
            final View view = inflater.inflate(R.layout.create_server_internet_dialog, null, false);
            final EditText et_server_name = view.findViewById(R.id.et_server_name);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Stwórz serwer przez Bluetooth dla 3 graczy")
                    .setMessage("Podaj nazwę serwera")
                    .setView(view)
                    .setPositiveButton("Stwórz", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            serverName = et_server_name.getText().toString()
                                    .replace(" ","").replace("$","S");
                            if (!serverName.isEmpty()) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                et_server_name.getText().clear();
                                discoverDevice = false;
                                cancelDiscoveryTimer();
                                Intent intent = new Intent(PlayBluetoothActivity.this, GameBluetoothActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("DEVICE_NAME", mBluetoothAdapter.getName());
                                intent.putExtra("DEVICE_ADDRESS", mBluetoothAdapter.getAddress());
                                intent.putExtra("PLAYER_NICK_NAME", playerNickName);
                                intent.putExtra("SERVER_NAME", serverName);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                showCreateServerDialog();
                                Toast.makeText(getApplicationContext(), "Wypełnij wszystkie pola!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            serverName = "";
                        }
                    })
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    //okno z informacją o uruchomieniu bt
    private void showEnableBTDialog() {
        AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Do prawidłowego działania gra wymaga włączenia Bluetooth")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enableBT();
                        showSetNickNameDialog();
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(PlayBluetoothActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    //uprawnienia
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

    //uprawnienia
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == COARSE_LOCATION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG,"COARSE_LOCATION PERMISSION_GRANTED");

            } else {
                Log.d(TAG,"COARSE_LOCATION PERMISSION_DENIED");
                startActivity(new Intent(PlayBluetoothActivity.this, MainActivity.class));
            }
        }
    }

    //uruchom bt
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

    //uruchom broadcast reciever
    public void enableDiscoverability() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
        startActivity(discoverableIntent);

    }



    private void enableDiscoverDevice() {
        if(mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.startDiscovery();
            IntentFilter filterDiscoverDevice = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filterDiscoverDevice.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
            Log.i(TAG, "Disccovering devices");
            registerReceiver(mPlayBroadcastReceiver, filterDiscoverDevice);

        }

    }


    public void scanAllBluetoothDevice() {
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
        mTimer.scheduleAtFixedRate(mTimerTask, 0, 10000);
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
                    mBTDevices.clear();
                    mBluetoothAdapter.startDiscovery();
                }
            }
        };
    }


    public void cancelDiscoveryTimer() {
        if (mTimer == null) {
            return;
        }
        mTimerTask = null;
        mTimer.cancel();
        mTimer = null;
    }

//    public static class MyReceiver extends BroadcastReceiver {
//
//        private final Handler handler; // Handler used to execute code on the UI thread
//
//        public MyReceiver(Handler handler) {
//            this.handler = handler;
//        }
//
//        @Override
//        public void onReceive(final Context context, Intent intent) {
//            // Post the UI updating code to our Handler
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(context, "Toast from broadcast receiver", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }




}



