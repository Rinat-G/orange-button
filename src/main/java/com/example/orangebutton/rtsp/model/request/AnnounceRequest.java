package com.example.orangebutton.rtsp.model.request;

public class AnnounceRequest extends CommonRequest {
    private final String sdpInfo;

    public AnnounceRequest(String path, int cSeq, String sdpInfo, long session) {
        super(Method.ANNOUNCE, path, cSeq, session);
        this.sdpInfo = sdpInfo;
    }

    public String getSdpInfo() {
        return sdpInfo;
    }

    @Override
    public String toString() {
        return "AnnounceRequest{" +
                "method=" + getMethod() +
                ", path='" + getPath() + '\'' +
                ", cSeq=" + getCSeq() +
                ", session=" + getSession() +
                ", sdpInfo='" + getSdpInfo() + '\'' +
                '}';
    }

}
