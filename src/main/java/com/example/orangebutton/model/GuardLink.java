package com.example.orangebutton.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GuardLink {
    private String signedLink;
    private String sizeMb;
}
