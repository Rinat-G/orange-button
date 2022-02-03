package com.example.orangebutton.rtsp;


import com.example.orangebutton.rtsp.model.RtpPacket;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LiveStreamHolder {
    private static final int QUEUE_CAPACITY = 10000;

    private final BlockingQueue<RtpPacket> rtpQueue;
    private final String sdpInfo;
    private final String[] transports = new String[2];


    public LiveStreamHolder(String sdpInfo) {
        this.rtpQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        this.sdpInfo = sdpInfo;
    }

    public BlockingQueue<RtpPacket> getRtpQueue() {
        return rtpQueue;
    }


    public String getSdpInfo() {
        return sdpInfo;
    }

    public String getTransport(int streamId) {
        return transports[streamId];
    }

    public void setTransport(int streamId, String transport) {
        transports[streamId] = transport;
    }
}
