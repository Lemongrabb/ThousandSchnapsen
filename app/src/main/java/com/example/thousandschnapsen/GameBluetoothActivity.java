/*
bluetoothManager.sendStringMessageExeptSpecifiedAddress(adresMac, wiadomość) - służy do wysyłania wiadomości



*/
package com.example.thousandschnapsen;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thousandschnapsen.bluetooth.BluetoothManager;
import com.example.thousandschnapsen.bluetooth.GameActivity;
import com.example.thousandschnapsen.bluetooth.eventBus.ClientConnectionFailEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.ClientConnectionSuccessEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.MessageSyncEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.ServerConnectionFailEvent;
import com.example.thousandschnapsen.bluetooth.eventBus.ServerConnectionSuccessEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



public class GameBluetoothActivity extends GameActivity {


}


