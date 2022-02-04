package com.example.orangebutton.controller;

import com.example.orangebutton.model.AuthRequest;
import com.example.orangebutton.service.SessionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public String getSession(@RequestBody AuthRequest request) {
        return sessionService.getSession(request.getIdToken());
    }
}
