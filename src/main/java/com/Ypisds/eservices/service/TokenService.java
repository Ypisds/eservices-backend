package com.Ypisds.eservices.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;
import java.time.*;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private final String secret = "ioaushduiqsn12k3oj1982u9xzj0i2190j34ointjfzxnoidzx";

    public String createToken(String username){
        try{
            Algorithm algorithm = Algorithm.HMAC384(secret);
            String token = JWT.create()
                    .withIssuer("eservices-api")
                    .withSubject(username)
                    .withExpiresAt(Instant.now().plus(3, ChronoUnit.HOURS))
                    .sign(algorithm);
            return token;
        }catch(JWTCreationException exception){
            throw new RuntimeException(exception.getMessage());
        }

    }

    public String validateToken(String token){
        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.HMAC384(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("eservices-api")
                    .build();

            decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException exception){
            return "";
        }
    }


}
