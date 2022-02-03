package com.example.orangebutton.rtsp;

import com.example.orangebutton.rtsp.exception.SocketClosedException;
import com.example.orangebutton.rtsp.model.request.AnnounceRequest;
import com.example.orangebutton.rtsp.model.request.CommonRequest;
import com.example.orangebutton.rtsp.model.request.SetupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Pattern;

import static com.example.orangebutton.rtsp.model.request.CommonRequest.Method;
import static java.lang.Integer.parseInt;


@Component
public class RTSPRequestParser {
    public static final Pattern BEFORE_RTSP_CASE_INS_PATTERN = Pattern.compile("(\\w+) (\\S+) RTSP", Pattern.CASE_INSENSITIVE);
    public static final Pattern PATH_CASE_INS_PATTERN = Pattern.compile("(\\w+) rtsp://\\S+?/(\\S+) RTSP", Pattern.CASE_INSENSITIVE);
    private static final Logger LOGGER = LoggerFactory.getLogger(RTSPRequestParser.class);

    public CommonRequest parse(BufferedReader reader) throws IOException {

        var requestString = readRawRtspRequest(reader);
        LOGGER.info("[c->s] \n" + requestString);

        //todo: rethink this behavior
        if (requestString.equals("")) {
            LOGGER.error("Got empty RTSP request");
            return null;
        }

        var method = parseMethod(requestString);
        int cSeq = parseCSeq(requestString);
        var path = parsePath(requestString);

        if (method == Method.ANNOUNCE) {
            int contentLength = parseContentLength(requestString);
            var sdpInfo = parseSdpInfo(reader, contentLength);
            LOGGER.info("[c->s] \n SDP payload: " + sdpInfo);
            return new AnnounceRequest(path, cSeq, sdpInfo, 0);
        }

        if (method == Method.SETUP) {
            var transport = parseTransport(requestString);
            return new SetupRequest(path, cSeq, transport, 0);
        }

        if (method == Method.UNKNOWN) {
            LOGGER.error("UNKNOWN Request: " + requestString);
        }

        return new CommonRequest(method, path, cSeq, 0);
    }

    private String readRawRtspRequest(BufferedReader reader) throws IOException {
        StringBuilder rawRequest = new StringBuilder();
        var needReadNext = true;

        while (needReadNext) {
            var line = reader.readLine();
            if (line == null) throw new SocketClosedException("Got null from input stream");

            if (line.length() > 2) { //more than "\n"
                rawRequest.append(line).append("\n");
            } else {
                needReadNext = false;
            }
        }
        return rawRequest.toString();
    }

    private String parseTransport(String requestLine) {
        var transportMatcher = Pattern.compile("Transport\\s*:\\s*(\\S+?(?=;mode))", Pattern.CASE_INSENSITIVE).matcher(requestLine);
        if (transportMatcher.find()) {
            return transportMatcher.group(1);
        } else {
            LOGGER.debug("Transport not found");
            return "";
        }
    }

    private String parseSdpInfo(BufferedReader reader, int contentLength) {
        char[] buffer = new char[contentLength];
        try {
            int count = reader.read(buffer, 0, contentLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buffer);
    }

    private int parseContentLength(String requestLine) {
        var cLenMatcher = Pattern.compile("Content-Length\\s*:\\s*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(requestLine);
        if (cLenMatcher.find()) {
            return parseInt(cLenMatcher.group(1));
        } else {
            LOGGER.debug("Content-Length not found");
            return -1;
        }
    }

    private int parseCSeq(String requestLine) {
        var cSeqMatcher = Pattern.compile("CSeq\\s*:\\s*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(requestLine);
        if (cSeqMatcher.find()) {
            return parseInt(cSeqMatcher.group(1));
        }
        LOGGER.debug("cSeq not found");
        return -1;
    }

    private String parsePath(String requestLine) {
        var matcher = PATH_CASE_INS_PATTERN.matcher(requestLine);
        if (matcher.find()) {
            return matcher.group(2);
        }
        LOGGER.warn("Path for RTSP request not found");
        return "";

//        if (!matcher.find()) throw new RuntimeException("Path parse error. Path not found in line: " + requestLine);
//        if (fullPath.endsWith("/"))
//            throw new RuntimeException("Invalid path name: can't end with a slash (" + fullPath + ")");

    }

    private Method parseMethod(String requestLine) {
        var matcher = BEFORE_RTSP_CASE_INS_PATTERN.matcher(requestLine);
        if (matcher.find()) {
            var method = matcher.group(1);
            if (method != null) {
                try {
                    return Method.valueOf(method);
                } catch (IllegalArgumentException O_o) {
                    return Method.UNKNOWN;
                }
            }
        }
        return Method.UNKNOWN;
    }


}
