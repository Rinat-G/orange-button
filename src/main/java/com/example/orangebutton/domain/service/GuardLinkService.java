package com.example.orangebutton.domain.service;

import com.example.orangebutton.model.GuardLink;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URI;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class GuardLinkService {
    private final S3Client s3Client;

    public GuardLinkService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public List<GuardLink> getLinks(String sessionName) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket("orange-button")
                .prefix(sessionName)
                .build();
        ListObjectsV2Response response = s3Client.listObjectsV2(request);

        var objects = response.contents();

        S3Presigner presigner = S3Presigner.builder()
                .endpointOverride(URI.create("https://storage.yandexcloud.net"))
                .build();

        var result = new ArrayList<GuardLink>();

        for (var object : objects) {
            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder()
                            .bucket("orange-button")
                            .key(object.key())
                            .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest =
                    presigner.presignGetObject(getObjectPresignRequest);
            DecimalFormat df = new DecimalFormat("###.##");
            result.add(new GuardLink(presignedGetObjectRequest.url().toString(), df.format(object.size() / 1_048_576f)));

        }
        return result;

    }
}
