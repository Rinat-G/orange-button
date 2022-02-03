package com.example.orangebutton.rtsp;

import java.net.Socket;

public class RTSPClient implements Runnable {
    private final Socket socket;
    private final RTSPClientHandler clientHandler;


    public RTSPClient(final Socket socket, RTSPClientHandler clientHandler) {
        this.socket = socket;
        this.clientHandler = clientHandler;
    }

    @Override
    public void run() {
        clientHandler.handle(socket);
    }
}
