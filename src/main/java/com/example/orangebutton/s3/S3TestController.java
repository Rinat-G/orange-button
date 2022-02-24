package com.example.orangebutton.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
public class S3TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3TestController.class);
    private final S3Client s3Client;

    public S3TestController(S3Client s3Client) {
        this.s3Client = s3Client;
    }

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
    public String s3PresignTest() {

        S3Presigner presigner = S3Presigner.builder().endpointOverride(URI.create("https://storage.yandexcloud.net"))
                .build();

        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket("orange-button")
                        .key("folder/test-video.ts")
                        .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        // Generate the presigned request
        PresignedGetObjectRequest presignedGetObjectRequest =
                presigner.presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();

    }

    @GetMapping("/s3List")
    public List<String> getList() {
        ListObjectsV2Request req = ListObjectsV2Request.builder().bucket("orange-button").prefix("7f00449e-2a16-4a00-a0ff-3950fdef05b8").build();
        ListObjectsV2Response response;

        response = s3Client.listObjectsV2(req);

        return response.contents().stream().map(S3Object::key).collect(toList());
    }
}
