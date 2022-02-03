package com.example.orangebutton.rtsp.jaffree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaffreeRunnable implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JaffreeRunnable.class);
    private final String path;
    private final JaffreeHandler jaffreeHandler;


    public JaffreeRunnable(String path, JaffreeHandler jaffreeHandler) {
        this.path = path;
        this.jaffreeHandler = jaffreeHandler;
    }

    @Override
    public void run() {
        LOGGER.debug("Jaffree thread started");
        jaffreeHandler.handle(path);
        LOGGER.debug("Jaffree thread stopped");
    }
}
