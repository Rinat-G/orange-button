package com.example.orangebutton.s3;

import com.example.orangebutton.rtsp.RTSPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@RestController
public class S3TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3TestController.class);


    @GetMapping("/s3test")
    public String s3Test() {

        S3Client s3 = S3Client.builder()
                .endpointOverride(URI.create("https://storage.yandexcloud.net"))
                .build();

        PutObjectRequest request = PutObjectRequest.builder().bucket("orange-button").key("folder/test-video.ts").build();
        RequestBody requestBody = RequestBody.fromFile(Path.of(System.getProperty("user.home") + "/Desktop/tmp/1.ts"));
        LOGGER.info("Upload started");

        var response = s3.putObject(request, requestBody);
        LOGGER.info("Upload ended");

        return response.toString();
//        return "Ok!";
    }

    @GetMapping("/s3PresignedUrlTest")
    public String s3PresignTest(){

        S3Presigner presigner = S3Presigner.builder().endpointOverride(URI.create("https://storage.yandexcloud.net"))
                .build();

        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket("orange-button")
                        .key("folder/test-video.ts")
                        .build();

        GetObjectPresignRequest getObjectPresignRequest =  GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        // Generate the presigned request
        PresignedGetObjectRequest presignedGetObjectRequest =
                presigner.presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();

    }
}
