package com.example.thousandschnapsen;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class GameInternetActivity extends AppCompatActivity {

    private Socket mSocket;
    String serverName = "";
    String playerId = "";
    String playerNickName = "";
    String max_players = "";
    String online_players = "";
    Boolean awaitingPlayers = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_internet);

        SocketIO app = (SocketIO) getApplication();
        mSocket = app.getSocket();

        serverName = getIntent().getStringExtra("SERVER_NAME");
        playerId = getIntent().getStringExtra("PLAYER_ID");
        playerNickName = getIntent().getStringExtra("PLAYER_NICK_NAME");

        final LayoutInflater inflater = LayoutInflater.from(GameInternetActivity.this);
        final View view = inflater.inflate(R.layout.awaiting_for_players_dialog, null, false);
        final TextView tv_number_of_players_online = view.findViewById(R.id.tv_number_of_players_online);
        final Button button_exit_server = view.findViewById(R.id.button_exit_server);

        button_exit_server.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSocket.disconnect();
                startActivity(new Intent(GameInternetActivity.this, MainActivity.class));
            }
        });

        final AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Oczekiwanie na graczy...")
            .setView(view)
            .create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        mSocket.emit("joinServer", serverName, playerId, playerNickName);

        mSocket.on("joinServer", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!(GameInternetActivity.this).isFinishing()) {
                            JSONObject data = (JSONObject) args[0];

                            try {
                                max_players = data.getString("max_players");
                                online_players = data.getString("online_players");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            tv_number_of_players_online.setText("Liczba graczy: " + online_players + " / " + max_players);

                            if (Integer.parseInt(online_players) < Integer.parseInt(max_players)) {
                                if (awaitingPlayers == false) {
                                    dialog.show();
                                    awaitingPlayers = true;
                                }
                                Toast.makeText(getApplicationContext(), "Liczba graczy: " + online_players + " / " + max_players, Toast.LENGTH_SHORT).show();
                            } else {
                                if (awaitingPlayers == true) {
                                    dialog.dismiss();
                                    awaitingPlayers = false;
                                }
                                Toast.makeText(getApplicationContext(), "Serwer gotowy! Liczba graczy: " + online_players + " / " + max_players, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        mSocket.on("serverDisabled", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    mSocket.disconnect();
                    startActivity(new Intent(GameInternetActivity.this, MainActivity.class));
                    }
                });
            }
        });

        final EditText editText = findViewById(R.id.messageText);
        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String data = editText.getText().toString();
                mSocket.emit("internetGameData", serverName, data); //SENDING DATA TO THE OTHERS CLIENTS IN THE CURRENT GAME GAME
            }
        });

        //RECEIVING DATA FROM THE OTHERS CLIENTS IN CURRENT GAME
        mSocket.on("internetGameData", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0]; //RECEIVED DATA FROM THE OTHERS CLIENT IN CURRENT GAME
                        Toast.makeText(getApplicationContext(), data,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

}
