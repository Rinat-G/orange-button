package com.example.orangebutton.rtsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class RTSPServer implements Runnable, ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RTSPServer.class);

    private final int port;
    private final RTSPClientHandler clientHandler;
    private final ExecutorService executorService = Executors.newCachedThreadPool(new CustomNameThreadFactory("p-rtsp-th-"));

    private Thread serverThread;


    public RTSPServer(@Value("${rtsp.server.port}") int port, RTSPClientHandler clientHandler) {
        this.port = port;
        this.clientHandler = clientHandler;
    }

    @Override
    public void run() {
        try (var serverSocket = new ServerSocket(port)) {

            LOGGER.info("RTSP Server Thread started on port " + port);
            while (!Thread.interrupted()) {
                var client = new RTSPClient(serverSocket.accept(), clientHandler);
                executorService.execute(client);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (serverThread == null || !serverThread.isAlive()) {
            serverThread = new Thread(this, "RTSP Server");
            serverThread.start();
        }
    }
}
