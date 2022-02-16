package com.example.orangebutton.web.controller;

import com.example.orangebutton.domain.service.SignUpService;
import com.example.orangebutton.model.SignUpRequest;
import com.example.orangebutton.model.UserDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signup")
public class SignUpController {

    private final SignUpService signUpService;

    public SignUpController(SignUpService signUpService) {
        this.signUpService = signUpService;
    }

    @PostMapping
    public UserDto signUp(@RequestBody SignUpRequest request, @RequestAttribute String email) {

        return signUpService.signUp(request, email);
    }
}
