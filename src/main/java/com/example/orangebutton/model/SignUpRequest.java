package com.example.orangebutton.model;

import lombok.Data;

import java.util.List;

@Data
public class SignUpRequest {
    private List<String> guardEmails;
    private String pin;
}
