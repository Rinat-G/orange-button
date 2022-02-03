package com.example.orangebutton.rtsp.jaffree;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;

@Component
public class JaffreeHandler {
    private static final String RTSP_LOCALHOST = "rtsp://localhost:%s/";

    private final String rtspUrl;
    private final String tempDir;

    public JaffreeHandler(@Value("${rtsp.server.port}") String rtspPort, @Value("${tmp.dir}") String tempDir) {
        rtspUrl = format(RTSP_LOCALHOST, rtspPort);
        this.tempDir = tempDir;
    }

    public void handle(String path) {
        //todo: refactor
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyy-MM-dd-HH-mm-ss");

        FFmpeg.atPath()
                .addInput(UrlInput.fromUrl(rtspUrl + path).addArguments("-rtsp_transport", "tcp"))
                .addArguments("-acodec", "copy")
                .addArguments("-vcodec", "copy")
                .addArguments("-f", "segment")
                .addArguments("-segment_time", "60")
//                .addArguments("-loglevel", "debug")
                .addOutput(UrlOutput.toUrl(tempDir + path + formatter.format(LocalDateTime.now()) + "_%03d.ts"))
//                .addOutput(UrlOutput.toUrl("./target/" + System.currentTimeMillis() + "-%03d.ts"))
                .execute();
    }
}
