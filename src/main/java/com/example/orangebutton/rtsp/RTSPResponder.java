package com.example.orangebutton.rtsp;

import com.example.orangebutton.rtsp.model.request.CommonRequest;
import com.example.orangebutton.rtsp.model.request.SetupRequest;
import org.springframework.stereotype.Component;

import static com.example.orangebutton.rtsp.model.request.CommonRequest.Method.*;

@Component
public class RTSPResponder {

    public static final String COMMON_OK_RESPONSE = "" +
            "RTSP/1.0 200 OK\r\n" +
            "CSeq: %d\r\n" +
            "Server: Orange Button Server\r\n";

    public static final String SUPPORTED_METHODS = "DESCRIBE, ANNOUNCE, SETUP, PLAY, RECORD";

    public static final String SDP_CONSTANT = "" +
            "o=- 0 0 IN IP4 127.0.0.1\r\n" +
            "s=Stream\r\n" +
            "c=IN IP4 0.0.0.0\r\n" +
            "t=0 0\r\n";

    public static final String MESSAGE_ENDING = "\r\n\r\n";
    private static final String BAD_REQUEST_RESPONSE = "RTSP/1.0 400 Bad Request";
    private static final String METHOD_NOT_ALLOWED = "RTSP/1.0 405 Method Not Allowed \r\n Allow: " + SUPPORTED_METHODS;

    private final LiveStreamsRegistry streamsRegistry;

    public RTSPResponder(LiveStreamsRegistry streamsRegistry) {
        this.streamsRegistry = streamsRegistry;
    }

    public String makeResponse(CommonRequest request) {

        if (request.getMethod() == OPTIONS) {
            return commonOk(request.getCSeq()) + "Public: " + SUPPORTED_METHODS + MESSAGE_ENDING;
        }

        if (request.getMethod() == DESCRIBE) {
            var sdpInfo = streamsRegistry.getLiveStream(request.getPath()).getSdpInfo();
            return commonOk(request.getCSeq()) +
                    //todo: contentBase needs adopt
//                    "Content-Base: rtsp://localhost:8554/stream2\r\n" +
                    "Content-Length: " + (sdpInfo.length() + SDP_CONSTANT.length()) + "\r\n" +
                    "Content-Type: application/sdp\r\n" +
                    "\r\n" +
                    SDP_CONSTANT +
                    sdpInfo;
        }

        if (request.getMethod() == SETUP) {
            String transport = streamsRegistry.getLiveStream(request.getPath()).getTransport(((SetupRequest) request).getStreamId());
            return commonOk(request.getCSeq()) +
                    "Transport: " + transport +
                    MESSAGE_ENDING;
        }

        if (request.getMethod() == PLAY) {
            return commonOk(request.getCSeq()) +
                    MESSAGE_ENDING;
        }

        if (request.getMethod() == ANNOUNCE || request.getMethod() == RECORD) {
            return commonOk(request.getCSeq()) + "\r\n";
        }

        if (request.getMethod() == UNKNOWN) {
            return BAD_REQUEST_RESPONSE + MESSAGE_ENDING;
        }

        return METHOD_NOT_ALLOWED + MESSAGE_ENDING;

    }

    private String commonOk(int cSeq) {
        return String.format(COMMON_OK_RESPONSE, cSeq);
    }
}
