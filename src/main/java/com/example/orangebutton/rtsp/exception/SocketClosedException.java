package com.example.orangebutton.rtsp.exception;

public class SocketClosedException extends RuntimeException {

    public SocketClosedException(){
        super();
    }
    public SocketClosedException(String message){
        super(message);
    }
}
