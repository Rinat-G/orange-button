package com.example.orangebutton.domain.service;

import com.example.orangebutton.dao.GuardDao;
import com.example.orangebutton.dao.UserDao;
import com.example.orangebutton.model.SignUpRequest;
import com.example.orangebutton.model.UserDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpService {

    private final UserDao userDao;
    private final GuardDao guardDao;
    private final PasswordEncoder passwordEncoder;

    public SignUpService(UserDao userDao, GuardDao guardDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.guardDao = guardDao;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto signUp(SignUpRequest request, String email) {
        var userid = userDao.insertNewUser(new UserDto(0, email, passwordEncoder.encode(request.getPin())));
        guardDao.insertGuardsForUser(userid, request.getGuardEmails());
        return new UserDto(userid, email, null);
    }
}
