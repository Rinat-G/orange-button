package com.example.orangebutton.service;

import com.example.orangebutton.dao.SessionDao;
import com.example.orangebutton.dao.UserDao;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Service
public class SessionService {

    public static final String NEW_SESSION_DEFAULT_STATUS = "open";
    private final UserDao userDao;
    private final SessionDao sessionDao;

    public SessionService(UserDao userDao, SessionDao sessionDao) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
    }

    public String getSession(String userEmail) {
        var user = requireNonNull(userDao.getUserByEmail(userEmail));


        var currentSession = sessionDao.getSessionByUserId(user.getUserId());
        if (currentSession != null) {
            return currentSession;
        }

        var newSession = UUID.randomUUID().toString();
        sessionDao.createNewSession(newSession, user.getUserId(), NEW_SESSION_DEFAULT_STATUS);
        return newSession;
    }
}
