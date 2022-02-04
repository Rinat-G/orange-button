package com.example.orangebutton.web.controller;

import com.example.orangebutton.domain.service.UserCheckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/check")
public class UserCheckController {

    private final UserCheckService userCheckService;

    public UserCheckController(UserCheckService userCheckService) {
        this.userCheckService = userCheckService;
    }

    @PostMapping
    public ResponseEntity<Void> checkUser(@RequestAttribute String email) {
        userCheckService.checkUser(email);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
