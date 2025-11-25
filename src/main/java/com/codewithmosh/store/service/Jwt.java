package com.codewithmosh.store.service;

import java.util.Date;

import javax.crypto.SecretKey;

import com.codewithmosh.store.entities.Role;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class Jwt {

    private final Claims claims;

    private final SecretKey secretKey;

    public Jwt(Claims claims,SecretKey secretKey){
        this.claims =claims;
        this.secretKey=secretKey;
    };

    public Long getId(){
            return Long.valueOf(claims.getSubject());
    }

    public Role getRole() {
    String roleString = claims.get("role", String.class);
    return Role.valueOf(roleString); // now matches YOUR enum
}
    
    public Boolean isExpired(){
         return claims.getExpiration().before(new Date());
    }

    public String toString(){
        return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }
    
}
