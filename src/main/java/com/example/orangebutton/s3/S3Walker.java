package com.example.orangebutton.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.lang.Thread.currentThread;

@Component
public class S3Walker implements Runnable, ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3Walker.class);

    private final String tempDir;
    private final S3Uploader s3Uploader;

    private Thread uploaderThread;

    public S3Walker(@Value("${tmp.dir}") String tempDir, S3Uploader s3Uploader) {
        this.tempDir = tempDir;
        this.s3Uploader = s3Uploader;
        var dir = new File(tempDir);
        if(!dir.exists() && !dir.mkdirs()){
            throw new RuntimeException("Can't create temp dir");
        }
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (uploaderThread == null || !uploaderThread.isAlive()) {
            uploaderThread = new Thread(this, "S3 Uploader");
            uploaderThread.start();
        }
    }

    @Override
    public void run() {
        while (!currentThread().isInterrupted()) {

            LOGGER.info("Checking files to upload");

            var oneMinuteAgo = Instant.now().minusSeconds(60);
            try (Stream<Path> paths = Files.walk(Paths.get(tempDir))) {
                //todo: refactor
                paths.filter(Files::isRegularFile)
                        .filter(path -> {
                            try {
                                return Files.readAttributes(path, BasicFileAttributes.class).creationTime().toInstant().isBefore(oneMinuteAgo);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }).forEach(s3Uploader::upload);
            } catch (IOException e) {
                e.printStackTrace();
            }


            //todo:
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }

}
