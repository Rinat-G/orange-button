package com.example.orangebutton.rtsp.model.request;

import static java.lang.Integer.parseInt;

public class SetupRequest extends CommonRequest {

    private final String transport;
    private final String fullPath;
    private final int streamId;

    public SetupRequest(String fullPath, int cSeq, String transport, long session) {
        super(Method.SETUP,  fullPath.substring(0, fullPath.lastIndexOf("/")), cSeq, session);
        this.transport = transport;
        this.fullPath = fullPath;
        this.streamId = parseInt(fullPath.substring(fullPath.length() - 1));
    }

    public String getTransport() {
        return transport;
    }

    public String getFullPath() {
        return fullPath;
    }

    public int getStreamId() {
        return streamId;
    }


    @Override
    public String toString() {
        return "SetupRequest{" +
                "method=" + getMethod() +
                ", path='" + getPath() + '\'' +
                ", cSeq=" + getCSeq() +
                ", session=" + getSession() +
                ", transport='" + getTransport() + '\'' +
                '}';
    }


}
