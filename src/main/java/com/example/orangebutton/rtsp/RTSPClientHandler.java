package com.example.orangebutton.rtsp;

import com.example.orangebutton.rtsp.exception.SocketClosedException;
import com.example.orangebutton.rtsp.jaffree.JaffreeHandler;
import com.example.orangebutton.rtsp.jaffree.JaffreeRunnable;
import com.example.orangebutton.rtsp.model.request.AnnounceRequest;
import com.example.orangebutton.rtsp.model.request.CommonRequest.Method;
import com.example.orangebutton.rtsp.model.request.SetupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.currentThread;

@Component
public class RTSPClientHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RTSPClientHandler.class);


    private final LiveStreamsRegistry streamsRegistry;
    private final RTSPRequestParser requestParser;
    private final RTSPPlaybackManager playbackManager;
    private final RTSPResponder responder;
    private final JaffreeHandler jaffreeHandler;
    private final ExecutorService jaffreeExecutorService = Executors.newCachedThreadPool(new CustomNameThreadFactory("p-jaffree-th-"));


    public RTSPClientHandler(LiveStreamsRegistry registry, RTSPRequestParser requestParser, RTSPPlaybackManager playbackManager, RTSPResponder responder, JaffreeHandler jaffreeHandler) {
        this.streamsRegistry = registry;
        this.requestParser = requestParser;
        this.playbackManager = playbackManager;
        this.responder = responder;
        this.jaffreeHandler = jaffreeHandler;
    }

    public void handle(final Socket socket) {
        LOGGER.info("Thread for rtsp client: {}:{} started", socket.getInetAddress().getHostAddress(), socket.getPort());
        try (
                var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                socket
        ) {
            //todo: stub while we have not sessions
            boolean isRecordSession = false;

            while (!currentThread().isInterrupted()) {
                var request = requestParser.parse(reader);

                if (request == null) {
                    continue;
                }

                LOGGER.debug("Received rtsp request: {}", request.toString());

                if (request.getMethod() == Method.ANNOUNCE) {
                    //todo: get rid
                    isRecordSession = true;
                    streamsRegistry.putNewLiveStream(request.getPath(), ((AnnounceRequest) request).getSdpInfo());
                }

                if (request.getMethod() == Method.SETUP && isRecordSession) {
                    var setupRequest = (SetupRequest) request;
                    streamsRegistry.getLiveStream(request.getPath()).setTransport(setupRequest.getStreamId(), setupRequest.getTransport());
                }

                String response = responder.makeResponse(request);
                LOGGER.info("[s->c] \n" + response);

                writer.write(response);
                writer.flush();


                if (request.getMethod() == Method.RECORD) {
                    LOGGER.info("RECORD started to path: {}", request.getPath());

                    jaffreeExecutorService.execute(new JaffreeRunnable(request.getPath(), jaffreeHandler));

                    playbackManager.doRecord(socket.getInputStream(), streamsRegistry.getLiveStream(request.getPath()).getRtpQueue());

                    LOGGER.info("RECORD stopped to path: {}", request.getPath());

                }

                if (request.getMethod() == Method.PLAY) {
                    LOGGER.info("PLAY started to path: {}", request.getPath());
                    playbackManager.doPlay(socket.getOutputStream(), streamsRegistry.getLiveStream(request.getPath()).getRtpQueue());
                    LOGGER.info("PLAY stopped to path: {}", request.getPath());

                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SocketClosedException e) {
            LOGGER.debug("Socket closed for rtsp client: {}:{}", socket.getInetAddress().getHostAddress(), socket.getPort());
        }

        LOGGER.info("Thread for rtsp client: {}:{} stopped", socket.getInetAddress().getHostAddress(), socket.getPort());

    }

}
