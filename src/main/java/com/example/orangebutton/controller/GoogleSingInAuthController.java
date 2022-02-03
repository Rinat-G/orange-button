package com.example.orangebutton.controller;

import com.example.orangebutton.model.AuthRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/auth")
public class GoogleSingInAuthController {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleSingInAuthController.class);
    private final GoogleIdTokenVerifier verifier;

    public GoogleSingInAuthController(GoogleIdTokenVerifier verifier) {
        this.verifier = verifier;
    }

    //todo: remove throws
    @PostMapping
    public ResponseEntity<Void> auth(@RequestBody AuthRequest request) throws GeneralSecurityException, IOException {
        if (request.getIdToken() != null) {
            LOG.info("Received request with idToken: {}", request.getIdToken());

            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken != null) {
                Payload payload = idToken.getPayload();

                String userId = payload.getSubject();
                LOG.info("User ID: {}", userId);

                String email = payload.getEmail();

            } else {
                LOG.error("Invalid ID token.");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }


            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
