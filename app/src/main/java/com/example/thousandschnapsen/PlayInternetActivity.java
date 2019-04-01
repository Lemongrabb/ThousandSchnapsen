package com.example.thousandschnapsen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class ServerInternetActivity extends AppCompatActivity {
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.3:3000");
        } catch (URISyntaxException e) {}
    }

    String playerId = "";
    String playerNickName = "";
    int numberOfPlayers = 0;
    String serverName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_internet);
        mSocket.connect();

        mSocket.on("getId", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];
                        playerId = data;
                    }
                });
            }
        });

        showSetNickNameDialog(ServerInternetActivity.this);

        final Button buttonCreateServer = findViewById(R.id.buttonCreateServer);
        buttonCreateServer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showCreateServerDialog(ServerInternetActivity.this);
            }
        });
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mSocket.disconnect();
//    }

    private void showSetNickNameDialog(Context c) {
        final EditText et_nickName = new EditText(c);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        et_nickName.setLayoutParams(lp);

        AlertDialog dialog = new AlertDialog.Builder(c)
            .setTitle("Graj przez Internet")
            .setMessage("Podaj swój Nick")
            .setView(et_nickName)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    playerNickName = et_nickName.getText().toString();
                    if(playerNickName.isEmpty()) {
                        showSetNickNameDialog(ServerInternetActivity.this);
                    }
                }
            })
            .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    playerId = "";
                    playerNickName = "";
                    startActivity(new Intent(ServerInternetActivity.this, MainActivity.class));
                }
            })
            .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showCreateServerDialog(Context c) {
        numberOfPlayers = 0;
        serverName = "";

        final LayoutInflater inflater = LayoutInflater.from(c);
        final View view = inflater.inflate(R.layout.create_server_internet_dialog, null, false);

        final EditText et_server_name = view.findViewById(R.id.et_server_name);
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

        AlertDialog dialog = new AlertDialog.Builder(c)
            .setTitle("Stwórz serwer")
            .setMessage("Wybierz liczbę graczy i podaj nazwę serwera")
            .setView(view)
            .setPositiveButton("Stwórz", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    serverName = et_server_name.getText().toString();
                    if((numberOfPlayers == 2 || numberOfPlayers == 3 || numberOfPlayers == 4) && !serverName.isEmpty()) {
                        JSONObject serverData = new JSONObject();
                        try {
                            serverData.put("server_owner_id", playerId);
                            serverData.put("server_owner_nick_name", playerNickName);
                            serverData.put("server_name", serverName);
                            serverData.put("number_of_players", numberOfPlayers);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mSocket.emit("createServer", serverData);
                        joinServer(serverName, playerId, playerNickName);
                    } else {
                        showCreateServerDialog(ServerInternetActivity.this);
                        Toast.makeText(getApplicationContext(),"Wypełnij wszystkie pola!",Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    numberOfPlayers = 0;
                    serverName = "";
                }
            })
            .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void joinServer(String serverName, String playerId, String playerNickName) {
        mSocket.emit("joinServer", serverName, playerId, playerNickName);
    }
}
