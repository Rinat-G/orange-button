package com.example.orangebutton.domain.service;

import com.example.orangebutton.dao.SessionDao;
import com.example.orangebutton.dao.UserDao;
import com.example.orangebutton.exception.PinCodeMismatchException;
import com.example.orangebutton.exception.UserNotFoundException;
import com.example.orangebutton.model.SessionCloseRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.lang.String.format;

@Service
public class SessionService {

    public static final String NEW_SESSION_DEFAULT_STATUS = "open";
    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final PasswordEncoder passwordEncoder;


    public SessionService(UserDao userDao, SessionDao sessionDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
        this.passwordEncoder = passwordEncoder;
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
        sessionDao.createNewSession(newSession, user.getUserId(), NEW_SESSION_DEFAULT_STATUS);
        return newSession.toString();
    }

    public void closeSession(SessionCloseRequest request, String email) {
        var currentUser = userDao.getUserByEmail(email);
        if (passwordEncoder.matches(request.getPinCode(), currentUser.getPinHash())) {
            sessionDao.closeSession(UUID.fromString(request.getSessionId()));
        } else {
            throw new PinCodeMismatchException();
        }
    }
}
