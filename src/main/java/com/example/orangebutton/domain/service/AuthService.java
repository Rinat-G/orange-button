package com.example.orangebutton.domain.service;

import com.example.orangebutton.exception.AuthenticationException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static java.lang.String.format;

@Service
public class AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    private final GoogleIdTokenVerifier verifier;

    public AuthService(GoogleIdTokenVerifier verifier) {
        this.verifier = verifier;
    }

    public String authenticate(@Nonnull String token) {
        LOG.info("Received request with idToken: {}", token);

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(token);
        } catch (GeneralSecurityException | IOException e) {
            throw new AuthenticationException(e);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException(format("Wrong token: %s\nCause: %s", token, e.getMessage()));
        }

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            LOG.info("User is authenticated. Email: {}", email);

            return email;

        } else {
            LOG.error("Token verification failed. Token: {}", token);
            throw new AuthenticationException("Token verification failed. Token: " + token);
        }
    }
}
