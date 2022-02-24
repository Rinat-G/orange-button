package com.example.orangebutton.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionCloseRequest {
    private String sessionId;
    private String pinCode;
}
