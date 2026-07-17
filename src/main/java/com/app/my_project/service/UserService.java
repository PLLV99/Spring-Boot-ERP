package com.app.my_project.service;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import com.app.my_project.WebConfig;

@Service
public class UserService {
    private String getSecret() {
        return WebConfig.getSecret();
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(getSecret());
    }

    public Long getUserIdFromToken(String token) {
        String tokenWithoutBearer = token.replace("Bearer ", "");

        if (tokenWithoutBearer.trim().isEmpty()) {
            throw new IllegalArgumentException("Token is empty");
        }

        return Long.valueOf(
                JWT.require(getAlgorithm())
                        .build()
                        .verify(tokenWithoutBearer)
                        .getSubject());

    }
}