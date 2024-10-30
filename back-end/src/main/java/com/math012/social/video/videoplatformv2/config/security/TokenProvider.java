package com.math012.social.video.videoplatformv2.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.math012.social.video.videoplatformv2.exception.TokenProviderException;
import com.math012.social.video.videoplatformv2.model.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.Date;

@Service
public class TokenProvider {


    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";

    private long validationTime = 18000000;



    public String tokenProvider(UserModel userModel){
        Date date = new Date();
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            String token = JWT.create()
                    .withIssuer("auth")
                    .withSubject(userModel.getUsername())
                    .withExpiresAt(new Date(date.getTime() + validationTime))
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new TokenProviderException("Your token was not created, try again");
        }
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.require(algorithm)
                    .withIssuer("auth")
                    .build()
                    .verify(token)
                    .getSubject();
        }
        catch (JWTVerificationException exception) {
            throw new TokenProviderException("Your token is not valid, try again!");
        }
    }
}