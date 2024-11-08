package org.example.util;

import java.util.Date;

import org.example.entity.User;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public class JWTUtil {

    private static final String SECRET_KEY = "mySecretKey";

    public static String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
            .withIssuer("auth0")
            .withSubject(user.getName())
            .withIssuedAt(new Date())
            .withClaim("roles", user.getRoles())
            .withClaim("email", user.getEmail())
            .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000))  // 1 hora de validade
            .sign(algorithm);
    }

    public static DecodedJWT verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build();
            return verifier.verify(token); // Decodifica e valida o token
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Invalid or expired token: " + e.getMessage());
        }
    }
}
