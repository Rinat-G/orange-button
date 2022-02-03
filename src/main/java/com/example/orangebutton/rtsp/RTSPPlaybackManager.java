package com.example.orangebutton.rtsp;


import com.example.orangebutton.rtsp.model.RtpPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.concurrent.BlockingQueue;

import static java.lang.Thread.currentThread;

@Component
public class RTSPPlaybackManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RTSPPlaybackManager.class);


    public void doPlay(OutputStream outputStream, BlockingQueue<RtpPacket> rtpQueue) {
        var header = new byte[]{(byte) '$', 0, 0, 0};
        while (!currentThread().isInterrupted()) {
            try {
                var packet = rtpQueue.take();
                if (packet.isTerminating()) {
                    LOGGER.info("Got terminating packet. Stop playing.");
                    break;
                }
//                LOGGER.debug("Taking packet from queue. Channel: {} Length: {}", packet.getChannel(), packet.getLength());

                header[1] = (byte) packet.getChannel();
                header[2] = (byte) (packet.getLength() >> 8);
                header[3] = (byte) (packet.getLength() & 0xFF);
                outputStream.write(header);
                outputStream.write(packet.getData());
                outputStream.flush();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void doRecord(InputStream inputStream, BlockingQueue<RtpPacket> rtpQueue) throws IOException {
        try {
            while (!currentThread().isInterrupted()) {
                var leadByte = inputStream.readNBytes(1);
                if (leadByte.length == 1 && leadByte[0] == (byte) '$') {
                    int channelNumber = inputStream.readNBytes(1)[0];
                    var lengthBytes = inputStream.readNBytes(2);
                    int length = ((lengthBytes[0] & 0xff) << 8) | (lengthBytes[1] & 0xff);//two-byte integer in network byte order to regular int

                    var data = inputStream.readNBytes(length);
                    RtpPacket packet = new RtpPacket(channelNumber, length, data);
                    offerWithOverflowHandling(rtpQueue, packet);

                } else if (leadByte.length == 0) {
                    LOGGER.info("Remote socket closed");
                    break;
                } else {
//                    todo:  error!
                    LOGGER.error("DO RECORD ERROR. Expected: '$' Found: {}{}", leadByte, new BufferedReader(new InputStreamReader(inputStream)).readLine());
                    break;
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //todo: weak point
            try {
                var terminatingPacket = new RtpPacket();
                offerWithOverflowHandling(rtpQueue, terminatingPacket);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //Try to offer. If queue is full we poll last packet and put new
    private void offerWithOverflowHandling(BlockingQueue<RtpPacket> rtpQueue, RtpPacket packet) throws InterruptedException {

        if (!rtpQueue.offer(packet)) {
            rtpQueue.poll();
            rtpQueue.put(packet);
        }
    }
}
