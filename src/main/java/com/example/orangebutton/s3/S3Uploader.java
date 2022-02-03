package com.example.orangebutton.s3;

import com.example.orangebutton.rtsp.RTSPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

@Component
public class S3Uploader {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3Uploader.class);


    public static final Pattern SESSION_ID_GROUP = Pattern.compile("(\\S+)_(\\S+_\\d+.ts)", Pattern.CASE_INSENSITIVE);
    private final static S3Client S3_CLIENT = S3Client.builder()
            .endpointOverride(URI.create("https://storage.yandexcloud.net"))
            .build();

    public void upload(Path path) {
        LOGGER.info("Uploading file {} to cloud", path.toString());
        var fileName = path.getFileName();
        var matcher = SESSION_ID_GROUP.matcher(fileName.toString());
        String sessionId = "";
        String file = "";
        if (matcher.find()) {
            sessionId = matcher.group(1);
            file = matcher.group(2);
        } else {
            //todo: throw? what exactly?
            LOGGER.error("Matcher find failed");
            throw new RuntimeException();
        }

        PutObjectRequest request = PutObjectRequest.builder().bucket("orange-button").key(sessionId + "/" + file).build();
        RequestBody requestBody = RequestBody.fromFile(path);
        S3_CLIENT.putObject(request, requestBody);

        //todo:
        LOGGER.info("Uploading complete. Deleting file {}", path.toString());
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
