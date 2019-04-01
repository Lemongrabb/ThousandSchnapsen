package com.example.thousandschnapsen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class PlayWifiActivity extends AppCompatActivity {

    String serverIp;
    String playerNickName;
    String playerId;
    private String serverName;
    private int max_player = 3;
    private int portUDP = 10000;

    ScheduledExecutorService scheduleTaskExecutor;//planowanie wykonania zadania
    DatagramSocket socketUDP; // socket dla komunikacji UDP
    DatagramPacket packetUDP; //pakiet presylany przez socket
    ListView servers_list_view;
    ArrayList<ServerWifi> serversData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_wifi);

        init();

        showSetNickNameDialog(PlayWifiActivity.this);

        scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                ServerWifiActivity.this.runOnUiThread(new Runnable() {
                    nasluch();
                }
            }
        }
    }

    private void init() {
        final Button buttonCreateServer = findViewById(R.id.buttonCreateServer);
        buttonCreateServer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showCreateServerDialog(PlayWifiActivity.this);
            }
        });
        servers_list_view = findViewById(R.id.servers_list_view);
    }

    private void showSetNickNameDialog(Context c) {
        final EditText et_nickName = new EditText(c);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        et_nickName.setLayoutParams(lp);

        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Graj przez Wifi")
                .setMessage("Podaj swój Nick")
                .setView(et_nickName)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playerNickName = et_nickName.getText().toString();
                        if (playerNickName.isEmpty()) {
                            showSetNickNameDialog(PlayWifiActivity.this);
                        }
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playerId = "";
                        playerNickName = "";
                        startActivity(new Intent(PlayWifiActivity.this, MainActivity.class));
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showCreateServerDialog(Context c) {
        max_player = 3;
        serverName = "";
        serverIp = getIP();
        final LayoutInflater inflater = LayoutInflater.from(c);
        final View view = inflater.inflate(R.layout.create_server_internet_dialog, null, false);
        final EditText et_server_name = view.findViewById(R.id.et_server_name);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Stwórz serwer w sieci lokalnej dla 3 graczy")
                .setMessage("Podaj nazwę serwera")
                .setView(view)
                .setPositiveButton("Stwórz", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        serverName = et_server_name.getText().toString();
                        if (!serverName.isEmpty()) {
                            scheduleTaskExecutor.shutdown();
                            socketUDP.close();
                            Intent serverData = new Intent(PlayWifiActivity.this, ServerWifiActivity.class);
                            serverData.putExtra("server_ip", serverIp);
                            serverData.putExtra("server_name", serverName);
                            serverData.putExtra("server_owner_id", playerId);
                            serverData.putExtra("server_owner_nick_name", playerNickName);
                            serverData.putExtra("max_player", max_player);
                            startActivity(serverData);
                        } else {
                            showCreateServerDialog(PlayWifiActivity.this);
                            Toast.makeText(getApplicationContext(), "Podaj nazwę serwera!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        max_player = 3;
                        serverName = "";
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    //---Narzedzia---\\
    //pobieranie adresu wifi telefonu
    String getIP() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    //nadanie odpowiednich uprawnien
    void uprawnienia() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    //nasluch broadcastu
    void nasluch() {
        uprawnienia(); //nadanie odpowiednich uprawnien
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final String text;
                byte[] message = new byte[1500];
                try {
                    packetUDP = new DatagramPacket(message, message.length);
                    socketUDP.receive(packetUDP);
                    text = new String(message, 0, packetUDP.getLength());
                    if (text != null) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                addToView(text);
                            }
                        });
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //dodaj do listy
    void addToView(String msg) {
        String[] tmpServerData = msg.split(",");
        //sprawdzenie bitu zycia
        ServerWifi tmpServer = new ServerWifi(tmpServerData[0], tmpServerData[1], tmpServerData[2], tmpServerData[3]);
        if (tmpServerData[4].equals("1")) {
            if (!serversData.contains(tmpServer)) {
                serversData.add(tmpServer);
                MyCustomAdapterWifi adapter = new MyCustomAdapterWifi(serversData, getApplicationContext());
                servers_list_view.setAdapter(adapter);
            }
        }
        if (tmpServerData[4].equals("0")) {
            if (serversData.contains(tmpServer)) {
                serversData.remove(tmpServer);
                MyCustomAdapterWifi adapter = new MyCustomAdapterWifi(serversData, getApplicationContext());
                servers_list_view.setAdapter(adapter);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduleTaskExecutor.shutdown();
        socketUDP.close();
        //TODO add more stuf
    }
}


class MyCustomAdapterWifi extends BaseAdapter implements ListAdapter {
    ArrayList<ServerWifi> arrayList;
    private Context context;

    public MyCustomAdapterWifi(ArrayList<ServerWifi> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ServerWifi serverData = arrayList.get(position);

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_list_view_internet_servers_list, null);
        }

        final String server_ip = serverData.server_ip;
        final String server_name = serverData.server_name;
        final String number_of_players = serverData.number_of_players;
        final String players_online = serverData.players_online;

        TextView serverNameText = view.findViewById(R.id.tv_server_name);
        serverNameText.setText(server_name);

        TextView numberOfPlayersText = view.findViewById(R.id.tv_number_of_players);
        numberOfPlayersText.setText(players_online + " / " + number_of_players);


        Button joinServerButton = view.findViewById(R.id.button_join_server);

        joinServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayWifiActivity PlayWifiActivity = new PlayWifiActivity();
                //PlayWifiActivity.joinServer(server_name, PlayWifiActivity.playerId, PlayWifiActivity.playerNickName);
                Intent intent = new Intent(context, GameWifiActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("SERVER_IP", server_ip);
                intent.putExtra("SERVER_NAME", server_name);
                intent.putExtra("PLAYER_ID", PlayWifiActivity.playerId);
                intent.putExtra("PLAYER_NICK_NAME", PlayWifiActivity.playerNickName);
                context.startActivity(intent);
            }
        });

        if (Integer.parseInt(number_of_players) == Integer.parseInt(players_online)) {
            joinServerButton.setEnabled(false);
        }

        return view;
    }
}

class ServerWifi {
    String server_ip;
    String server_name;
    String players_online;
    String number_of_players;

    ServerWifi(String server_ip, String server_name, String players_online, String number_of_players) {
        super();
        this.server_ip = server_ip;
        this.server_name = server_name;
        this.players_online = players_online;
        this.number_of_players = number_of_players;
    }

    public String getServerIP() {
        return server_ip;
    }

    public String getServerName() {
        return server_name;
    }

    public String getPlayersOnline() {
        return players_online;
    }

    public String numberOfPlayers() {
        return number_of_players;
    }

    @Override
    public boolean equals(Object obj) {
        return (this.server_ip.equals(((ServerWifi) obj).server_ip)
                && this.server_name.equals(((ServerWifi) obj).server_name)
                && this.players_online.equals(((ServerWifi) obj).players_online)
                && this.number_of_players.equals(((ServerWifi) obj).number_of_players));
    }
}
