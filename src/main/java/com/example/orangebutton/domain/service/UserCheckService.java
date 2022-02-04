package com.example.orangebutton.domain.service;

import com.example.orangebutton.dao.UserDao;
import com.example.orangebutton.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class UserCheckService {
    private final UserDao userDao;

    public UserCheckService(
            UserDao userDao
    ) {
        this.userDao = userDao;
    }

    public void checkUser(String email) {
        var user = userDao.getUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException(format("User with email %s not found", email));
        }
    }
}
