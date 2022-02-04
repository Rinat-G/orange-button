package com.example.orangebutton.service;

import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private final AuthService authService;
    private final GetSessionService getSessionService;

    public SessionService(AuthService authService, GetSessionService getSessionService) {
        this.authService = authService;
        this.getSessionService = getSessionService;
    }

    public String getSession(String token) {
        var email = authService.authenticate(token);

        return getSessionService.getSession(email);
    }
}
