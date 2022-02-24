package com.example.orangebutton.web.controller;

import com.example.orangebutton.domain.service.SessionService;
import com.example.orangebutton.model.SessionCloseRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/session")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public String getSession(@RequestAttribute String email) {
        return sessionService.getSession(email);
    }

    @PostMapping("/close")
    public void closeSession(@RequestBody SessionCloseRequest request, @RequestAttribute String email) {
        sessionService.closeSession(request, email);
    }
}
