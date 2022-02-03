package com.example.orangebutton.rtsp.model;

import java.util.Arrays;
import java.util.Objects;

public class RtpPacket {
    private final int channel;
    private final int length;
    private final byte[] data;
    private final boolean isTerminating;

    public RtpPacket(int channel, int length, byte[] data) {
        this.channel = channel;
        this.length = length;
        this.data = data;
        this.isTerminating = false;
    }


    /**
     * Creates empty terminating packet.
     * Terminating packet need for consumer to determine end of stream.
     */
    public RtpPacket() {
        this.isTerminating = true;
        this.channel = -1;
        this.length = -1;
        this.data = null;
    }


    public int getChannel() {
        return channel;
    }

    public int getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isTerminating() {
        return isTerminating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RtpPacket rtpPacket = (RtpPacket) o;
        return getChannel() == rtpPacket.getChannel() && getLength() == rtpPacket.getLength() && Arrays.equals(getData(), rtpPacket.getData());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getChannel(), getLength());
        result = 31 * result + Arrays.hashCode(getData());
        return result;
    }
}
