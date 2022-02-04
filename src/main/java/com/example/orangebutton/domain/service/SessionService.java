package com.example.orangebutton.domain.service;

import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private final GetSessionService getSessionService;

    public SessionService(GetSessionService getSessionService) {
        this.getSessionService = getSessionService;
    }

    public String getSession(String email) {

        return getSessionService.getSession(email);
    }
}
