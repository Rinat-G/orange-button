package com.example.orangebutton.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private long userId;
    private String userEmail;
    private String pinHash;
}
