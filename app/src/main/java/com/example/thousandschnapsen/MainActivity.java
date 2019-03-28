package com.example.thousandschnapsen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button buttonPlayInternet = findViewById(R.id.buttonPlayInternet);
        buttonPlayInternet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PlayInternetActivity.class));
            }
        });

        final Button buttonPlayBluetooth = findViewById(R.id.buttonPlayBluetooth);
        buttonPlayBluetooth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PlayBluetoothActivity.class));
            }
        });
    }
}
