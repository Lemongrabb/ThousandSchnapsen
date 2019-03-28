package com.example.thousandschnapsen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlayInternetActivity extends AppCompatActivity {
    private Socket mSocket;
    public static String playerId = "";
    public static String playerNickName = "";
    int numberOfPlayers = 3;
    String serverName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_internet);

        SocketIO app = (SocketIO) getApplication();
        mSocket = app.getSocket();
        if(!mSocket.connected()) {
            mSocket.connect();
        }

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

        mSocket.on("getServers", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray data = (JSONArray) args[0];
                        ArrayList<Server> serversData = new ArrayList<Server>();

                        for(int i=0; i<data.length(); i++) {
                            try {
                                JSONObject obj = data.getJSONObject(i);
                                String server_name = obj.getString("server_name");
                                String number_of_players = obj.getString("number_of_players");
                                String players_online = obj.getString("players_online");

                                serversData.add(new Server(server_name, number_of_players, players_online));
                                //System.out.println(server_name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        MyCustomAdapter adapter = new MyCustomAdapter(serversData, getApplicationContext());

                        ListView serversListView = (ListView) findViewById(R.id.servers_list_view);
                        serversListView.setAdapter(adapter);

                    }
                });
            }
        });

        showSetNickNameDialog(PlayInternetActivity.this);

        final Button buttonCreateServer = findViewById(R.id.buttonCreateServer);
        buttonCreateServer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showCreateServerDialog(PlayInternetActivity.this);
            }
        });
    }

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
                        showSetNickNameDialog(PlayInternetActivity.this);
                    }
                }
            })
            .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    playerId = "";
                    playerNickName = "";
                    mSocket.disconnect();
                    startActivity(new Intent(PlayInternetActivity.this, MainActivity.class));
                }
            })
            .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showCreateServerDialog(Context c) {
        numberOfPlayers = 3;
        serverName = "";

        final LayoutInflater inflater = LayoutInflater.from(c);
        final View view = inflater.inflate(R.layout.create_server_internet_dialog, null, false);

        final EditText et_server_name = view.findViewById(R.id.et_server_name);
//        final RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
//
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if(checkedId == R.id.rb_two_players) {
//                    numberOfPlayers = 2;
//                } else if(checkedId == R.id.rb_three_players) {
//                    numberOfPlayers = 3;
//                } else if(checkedId == R.id.rb_four_players) {
//                    numberOfPlayers = 4;
//                }
//            }
//        });

        AlertDialog dialog = new AlertDialog.Builder(c)
            .setTitle("Stwórz serwer w sieci Internet dla 3 graczy")
            .setMessage("Podaj nazwę serwera")
            .setView(view)
            .setPositiveButton("Stwórz", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    serverName = et_server_name.getText().toString();
                    if(/*(numberOfPlayers == 2 || numberOfPlayers == 3 || numberOfPlayers == 4) &&*/ !serverName.isEmpty()) {
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
                        showCreateServerDialog(PlayInternetActivity.this);
                        Toast.makeText(getApplicationContext(),"Podaj nazwę serwera!",Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    numberOfPlayers = 3;
                    serverName = "";
                }
            })
            .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void joinServer(String serverName, String playerId, String playerNickName) {
        //System.out.println(serverName + " "  + playerId + " " + playerNickName);
        Intent intent = new Intent(getBaseContext(), GameInternetActivity.class);
        intent.putExtra("SERVER_NAME", serverName);
        intent.putExtra("PLAYER_ID", playerId);
        intent.putExtra("PLAYER_NICK_NAME", playerNickName);
        startActivity(intent);
    }
}

class MyCustomAdapter extends BaseAdapter implements ListAdapter {
    ArrayList<Server> arrayList;
    private Context context;

    public MyCustomAdapter(ArrayList<Server> arrayList, Context context) {
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
        Server serverData =arrayList.get(position);

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_list_view_internet_servers_list, null);
        }

        final String server_name = serverData.server_name;
        final String number_of_players = serverData.number_of_players;
        final String players_online = serverData.players_online;

        TextView serverNameText = (TextView)view.findViewById(R.id.tv_server_name);
        serverNameText.setText(server_name);

        TextView numberOfPlayersText = (TextView)view.findViewById(R.id.tv_number_of_players);
        numberOfPlayersText.setText(players_online +" / " + number_of_players);


        Button joinServerButton = (Button) view.findViewById(R.id.button_join_server);

        joinServerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PlayInternetActivity PlayInternetActivity = new PlayInternetActivity();
                //PlayInternetActivity.joinServer(server_name, PlayInternetActivity.playerId, PlayInternetActivity.playerNickName);
                Intent intent = new Intent(context, GameInternetActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("SERVER_NAME", server_name);
                intent.putExtra("PLAYER_ID", PlayInternetActivity.playerId);
                intent.putExtra("PLAYER_NICK_NAME", PlayInternetActivity.playerNickName);
                context.startActivity(intent);
            }
        });

        if(Integer.parseInt(number_of_players) == Integer.parseInt(players_online)) {
            joinServerButton.setEnabled(false);
        }

        return view;
    }
}

class Server {
    String server_name;
    String number_of_players;
    String players_online;

    public Server(String server_name, String number_of_players, String players_online) {
        this.server_name = server_name;
        this.number_of_players = number_of_players;
        this.players_online = players_online;
    }
}
