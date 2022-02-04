package com.example.orangebutton.web.controller;

import com.example.orangebutton.model.SignUpRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
public class SignUpController {

    @PostMapping
    public Void signUp(@RequestBody SignUpRequest request, @RequestParam String token) {
        return null;
    }
}
