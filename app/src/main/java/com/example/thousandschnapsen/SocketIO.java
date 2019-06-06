package com.example.thousandschnapsen;

import android.app.Application;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SocketIO extends Application {

    private Socket mSocket;
    {
        try {
            IO.Options opts = new IO.Options();
            opts.path = Constants.SERVER_SOCKET_IO_PATH;

            mSocket = IO.socket(Constants.SERVER_URL, opts);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
