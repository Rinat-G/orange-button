package com.example.orangebutton.rtsp;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LiveStreamsRegistry {
//    private static final LiveStreamsRegistry INSTANCE = new LiveStreamsRegistry();
    private final Map<String, LiveStreamHolder> registry;

    private LiveStreamsRegistry() {
        registry = new HashMap<>();
    }

//    public static LiveStreamsRegistry getInstance() {
//        return INSTANCE;
//    }

    public void putNewLiveStream(String path, String sdpInfo) {
        registry.put(path, new LiveStreamHolder(sdpInfo));
    }

    public LiveStreamHolder getLiveStream(String path) {
        return registry.get(path);
    }
}
