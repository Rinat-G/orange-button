package com.example.orangebutton.domain.service;

import com.example.orangebutton.dao.SessionDao;
import com.example.orangebutton.dao.UserDao;
import com.example.orangebutton.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.lang.String.format;

@Service
public class GetSessionService {

    public static final String NEW_SESSION_DEFAULT_STATUS = "open";
    private final UserDao userDao;
    private final SessionDao sessionDao;

    public GetSessionService(UserDao userDao, SessionDao sessionDao) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
    }

    public String getSession(String userEmail) {
        var user = userDao.getUserByEmail(userEmail);
        if (user == null) {
            throw new UserNotFoundException(format("User with email %s not found", userEmail));
        }

        var currentSession = sessionDao.getSessionByUserId(user.getUserId());
        if (currentSession != null) {
            return currentSession;
        }

        var newSession = UUID.randomUUID();
//        var newSession = UUID.randomUUID().toString();
        sessionDao.createNewSession(newSession, user.getUserId(), NEW_SESSION_DEFAULT_STATUS);
        return newSession.toString();
    }
}
