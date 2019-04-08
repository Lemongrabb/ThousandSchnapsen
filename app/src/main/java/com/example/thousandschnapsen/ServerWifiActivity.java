package com.example.thousandschnapsen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ServerWifiActivity extends AppCompatActivity {

    private static Context context;
    //zmienne gui
    EditText edtText;
    Button btnSend;

    String serverIp;
    String serverName;
    String playerId;
    String playerNickName;
    static int maxPlayer = 3;
    //udp brodcast
    int portUDP = 10000;
    static int onlinePlayers = 1;
    ScheduledExecutorService scheduleTaskExecutor;
    //tcp
    int portTCP = 11000;// The default port number.
    private ServerSocket serverSocket;
    private Thread serverThread;
    private Socket clientSocket;
    private final clientThread[] threads = new clientThread[maxPlayer];
    private static Handler handler;
    private static TextView tv_number_of_players_online;
    private static AlertDialog dialogAwating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_wifi);

        ServerWifiActivity.context = getApplicationContext();

        Intent intent = getIntent();
        serverIp = intent.getStringExtra("server_ip");
        serverName = intent.getStringExtra("server_name");
        playerId = intent.getStringExtra("server_owner_id");
        playerNickName = intent.getStringExtra("server_owner_nick_name");
        maxPlayer = intent.getIntExtra("max_player", 3);

        if (getIP().equals("")||getIP().equals("0.0.0.0")) {
            showNoIpAddres(ServerWifiActivity.this);
        } else {
            handler = new Handler();

            edtText = findViewById(R.id.editText);
            edtText.setText(playerNickName);
            btnSend = findViewById(R.id.button);
            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getIP().equals("")||getIP().equals("0.0.0.0")) {
                        showNoIpAddres(ServerWifiActivity.this);
                    } else {
                        String msg = edtText.getText().toString();
                        showMessage(msg);
                        clientThread.sendToAll(msg);
                    }
                }
            });

            final LayoutInflater inflater = LayoutInflater.from(ServerWifiActivity.this);
            final View view = inflater.inflate(R.layout.awaiting_for_players_dialog, null, false);
            tv_number_of_players_online = view.findViewById(R.id.tv_number_of_players_online);
            final Button button_exit_server = view.findViewById(R.id.button_exit_server);
            button_exit_server.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(ServerWifiActivity.this, MainActivity.class));
                    finish();
                }
            });

            tv_number_of_players_online.setText("Liczba graczy: " + onlinePlayers + " / " + maxPlayer);

            dialogAwating = new AlertDialog.Builder(this)
                    .setTitle("Oczekiwanie na graczy...")
                    .setView(view)
                    .create();
            dialogAwating.setCancelable(false);
            dialogAwating.setCanceledOnTouchOutside(false);

            dialogAwating.show();

            scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
            scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    ServerWifiActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //serverData = nazwaSerwera + IpSerwera+ ilocsGracz + maxGraczy + bitZycia
                            sendBroadcast(serverIp + "," + serverName + "," + onlinePlayers + "," + maxPlayer + ",", "1");
                        }
                    });
                }
            }, 0, 1, SECONDS);

            this.serverThread = new Thread(new ServerThread());
            this.serverThread.start();
        }
    }

    private void showNoIpAddres(Context c) {
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Błąd połączenia z siecią lokalną")
                .setMessage("Nie jesteś połączony z siecią wifi lub nie posiadasz prawidłowo nadanego adresu ip.")
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ServerWifiActivity.this, MainActivity.class));
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    //pobieranie adresu wifi telefonu
    private String getIP() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    //pobieranie adresu broadcast
    private InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    //nadanie odpowiednich uprawnien
    private void uprawnienia() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    //Oglaszanie serwera przez broadcast
    private void sendBroadcast(String msg, String bit) {
        uprawnienia();
        try {
            DatagramSocket socketUPD = new DatagramSocket(portUDP);
            socketUPD.setBroadcast(true);
            String messageStr = msg + bit;
            byte[] sendData = messageStr.getBytes();
            DatagramPacket p = new DatagramPacket(sendData, sendData.length, getBroadcastAddress(), portUDP);
            socketUPD.send(p);
            socketUPD.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showMessage(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void changeText(final int onlinePlayers) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                if (onlinePlayers == maxPlayer) {
                    dialogAwating.dismiss();
                }
                if (onlinePlayers > maxPlayer) {
                    dialogAwating.dismiss();
                    tv_number_of_players_online.setText("Liczba graczy: " + onlinePlayers + " / " + maxPlayer);
                    dialogAwating.show();
                }
            }
        });
    }

    private class ServerThread implements Runnable {
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(portTCP);
            } catch (IOException e) {
                System.out.println(e);
            }
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    int i = 0;
                    for (i = 0; i < maxPlayer; i++) {
                        if (threads[i] == null) {
                            (threads[i] = new clientThread(clientSocket, threads)).start();
                            break;
                        }
                    }
                    if (i == maxPlayer) {
                        PrintStream os = new PrintStream(clientSocket.getOutputStream());
                        os.println("Server too busy. Try later.");
                        os.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
    }

    static class clientThread extends Thread {
        private String clientName = null;
        private DataInputStream is = null;
        private PrintStream os = null;
        private Socket clientSocket = null;
        private static clientThread[] threads;
        private static int maxClientsCount;

        public clientThread(Socket clientSocket, clientThread[] threads) {
            this.clientSocket = clientSocket;
            this.threads = threads;
            maxClientsCount = threads.length;
        }

        public void run() {
            int maxClientsCount = this.maxClientsCount;
            clientThread[] threads = this.threads;

            try {
                is = new DataInputStream(clientSocket.getInputStream());
                os = new PrintStream(clientSocket.getOutputStream());
                String name;
                while (true) {
                    name = is.readLine();
                    if (name.indexOf('@') == -1)
                        break;
                }

                os.println("Witaj " + name + "na naszym serwerze.");
                onlinePlayers++;
                changeText(onlinePlayers);
                synchronized (this) {
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i] == this) {
                            clientName = "@" + name;
                            break;
                        }
                    }
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i] != this) {
                            threads[i].os.println("Nowy uzytkownik na naszym serwerze:" + name);
                        }
                    }
                }
                /* Start the conversation. */
                while (true) {
                    String line = is.readLine();
                    if (line == null||line.startsWith("Disconnect")) {
                        break;
                    }

                    /* The message is public, broadcast it to all other clients. */
                    synchronized (this) {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i].clientName != null) {
                                threads[i].os.println(line);
                            }
                        }
                        ServerWifiActivity.showMessage(line);
                    }
                }
                synchronized (this) {
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
                            threads[i].os.println("*** The user " + name + " is leaving the chat room !!! ***");
                        }
                    }
                }
                os.println("*** Bye " + name + " ***");

                synchronized (this) {
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] == this) {
                            threads[i] = null;
                        }
                    }
                }
                is.close();
                os.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        static void sendToAll(String line) {
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i].clientName != null) {
                    threads[i].os.println(line);
                }
            }
        }
    }

    //TODO dopisac zamkniecie komunikacji po TCP
    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduleTaskExecutor.shutdown();
        sendBroadcast(serverIp + "," + serverName + "," + onlinePlayers + "," + maxPlayer + ",", "0");
        try {
            serverSocket.close();
            if(clientSocket!=null) {
                clientSocket.shutdownOutput();
                clientSocket.shutdownInput();
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
