package com.codewithmosh.store.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {

    @Value("${spring.jwt.secret}")
    private String secret;


    

    public String generateJwtTocken(String email){
        final long tokenExpiration=8400;

        return Jwts.builder()
            .subject(email)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis()+1000*tokenExpiration))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
            .compact();
    
    }

    public Boolean validateTocken(String tocken){
        try{
        var Claims =Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(tocken)
                .getPayload();

        return Claims.getExpiration().after(new Date());
        }catch(JwtException ex){
            return false;

        }

    }

    
}
