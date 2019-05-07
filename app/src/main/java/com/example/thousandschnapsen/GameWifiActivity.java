package com.example.thousandschnapsen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameWifiActivity extends AppCompatActivity {
    //zmienne gui
    EditText edtText;
    Button btnSend;
    private String playerNickname;
    private static String serverIP;
    private String serverName;
    private static int maxPlayers = 3;
    private int onlinePlayers;
    private static Handler handler;
    static final int portTCP = 11000;
    private ClientThread clientThread;
    private static TextView tv_number_of_players_online;
    static AlertDialog dialogAwating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_wifi);

        Intent intent = getIntent();
        serverIP = intent.getStringExtra("SERVER_IP");
        serverName = intent.getStringExtra("SERVER_NAME");
        playerNickname = intent.getStringExtra("PLAYER_NICK_NAME");
        onlinePlayers = intent.getIntExtra("PLAYER_ONLINE", 2);
        maxPlayers = intent.getIntExtra("MAX_PLAYER", 3);

        if (getIP().equals("")||getIP().equals("0.0.0.0")) {
            showNoIpAddres(GameWifiActivity.this);
        } else {
            handler = new Handler();

            clientThread = new ClientThread();
            Thread thread = new Thread(clientThread);
            thread.start();

            edtText = findViewById(R.id.editText);
            edtText.setText(playerNickname);
            btnSend = findViewById(R.id.button);
            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String msg = edtText.getText().toString().trim();
                    if (null != clientThread) {
                        if (getIP().equals("")||getIP().equals("0.0.0.0")) {
                            showNoIpAddres(GameWifiActivity.this);
                        } else {
                            clientThread.sendMessage(msg);
                        }
                    }
                }
            });

            final LayoutInflater inflater = LayoutInflater.from(GameWifiActivity.this);
            final View view = inflater.inflate(R.layout.awaiting_for_players_dialog, null, false);
            tv_number_of_players_online = view.findViewById(R.id.tv_number_of_players_online);
            final Button button_exit_server = view.findViewById(R.id.button_exit_server);
            button_exit_server.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(GameWifiActivity.this, MainActivity.class));
                    finish();
                }
            });

            tv_number_of_players_online.setText("Liczba graczy: " + onlinePlayers + " / " + maxPlayers);

            dialogAwating = new AlertDialog.Builder(this)
                    .setTitle("Oczekiwanie na graczy...")
                    .setView(view)
                    .create();
            dialogAwating.setCancelable(false);
            dialogAwating.setCanceledOnTouchOutside(false);
            dialogAwating.show();
        }
    }

    String getIP() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    public void showMessage(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void changeText(final int onlinePlayers) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                if (onlinePlayers == maxPlayers) {
                    dialogAwating.dismiss();
                }
                if (onlinePlayers > maxPlayers) {
                    dialogAwating.dismiss();
                    tv_number_of_players_online.setText("Liczba graczy: " + onlinePlayers + " / " + maxPlayers);
                    dialogAwating.show();
                }
            }
        });
    }

    class ClientThread implements Runnable {
        Socket socket;
        private BufferedReader input;

        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIP);
                socket = new Socket(serverAddr, portTCP);
                sendMessage(playerNickname);
                while (!Thread.currentThread().isInterrupted()) {
                    this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message = input.readLine();
                    if (null == message || "Disconnect".contentEquals(message)) {
                        Thread.interrupted();
                        message = "Server Disconnected.";
                        showMessage(message);
                        startActivity(new Intent(GameWifiActivity.this, MainActivity.class));
                        break;
                    }
                    if(message.startsWith("Nowy")) {
                        changeText(++onlinePlayers);
                    }
                    showMessage(message);
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        void sendMessage(final String message) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (null != socket) {
                            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                            out.println(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    private void showNoIpAddres(Context c) {
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(c)
                .setTitle("Błąd połączenia z siecią lokalną")
                .setMessage("Nie jesteś połączony z siecią wifi lub nie posiadasz prawidłowo nadanego adresu ip.")
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(GameWifiActivity.this, MainActivity.class));
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( clientThread!= null) {
            clientThread.sendMessage("Disconnect");
            try {
                clientThread.socket.shutdownInput();
                clientThread.socket.shutdownOutput();
                clientThread.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}