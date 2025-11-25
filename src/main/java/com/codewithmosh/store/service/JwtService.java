package com.codewithmosh.store.service;

import java.util.Date;
import java.lang.String;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.dto.LoginUserDto;
import com.codewithmosh.store.entities.Role;
import com.codewithmosh.store.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class JwtService {

    private JwtConfig jwtConfig;
    public Jwt generateJwtTocken(User user){
        final long tokenExpiration=jwtConfig.getAccessTockenExpiration();

        return generateTocken(user, tokenExpiration);
    }

    public Jwt genrerateRefreshTocken(User user){
        final long tockenExpiration = jwtConfig.getRefreshTockenExpiration();

        return generateTocken(user, tockenExpiration);
    }

    public Jwt generateTocken(User user,long tokenExpiration){

        var claims=Jwts.claims()
                    .subject(user.getId().toString())
                    .add("email", user.getEmail())
                    .add("name",user.getName())
                    .add("role", user.getRole())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis()+1000*tokenExpiration))
                    .build();

        
        return new Jwt(claims,jwtConfig.getSecretKey());
        

    }

    public Jwt parseTocken(String tocken){
        try{
            var claims = getClaims(tocken);
            return new Jwt(claims, jwtConfig.getSecretKey());
        }
        catch(JwtException e) {
            return null;
        }

    }

    


    


    private Claims getClaims(String tocken){

        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(tocken)
                .getPayload();
    }

    
}
