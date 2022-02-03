package com.example.orangebutton.rtsp.model.request;

public class CommonRequest {
    private final Method method;
    private final String path;
    private final int cSeq;

    private final long session;

    public CommonRequest(Method method, String path, int cSeq,
                         long session
    ) {
        this.method = method;
        this.path = path;
        this.cSeq = cSeq;
        this.session = session;
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getCSeq() {
        return cSeq;
    }

    public long getSession() {
        return session;
    }


    @Override
    public String toString() {
        return "ClientRequest{" +
                "method=" + method +
                ", path='" + path + '\'' +
                ", cSeq=" + cSeq +
                ", session=" + session +
                '}';
    }

    public enum Method {
        ANNOUNCE,
        DESCRIBE,
        OPTIONS,
        PLAY,
        PAUSE,
        RECORD,
        SETUP,
        TEARDOWN,
        UNKNOWN
    }

}
