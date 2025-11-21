package com.codewithmosh.store.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewithmosh.store.dto.LoginUserDto;
import com.codewithmosh.store.dto.JwtTockenDto;
import com.codewithmosh.store.service.JwtService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/login")
@AllArgsConstructor
public class AuthController {

    private AuthenticationManager authenticationManager;

    private JwtService jwtService;



    @PostMapping
    public ResponseEntity<JwtTockenDto> loginUser(
        @Valid@RequestBody LoginUserDto request
    ){ 

      authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
      );

      var tocken = jwtService.generateJwtTocken(request.getEmail());
        
      return ResponseEntity.ok(new JwtTockenDto(tocken));

    }

    @PostMapping("/validate")
    public boolean validateTocken(@RequestHeader("Authorization") String authHeader){

      var tocken = authHeader.replace("Barear ", "");

      return jwtService.validateTocken(tocken);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException(){

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }
 
    
}
