package com.codewithmosh.store.controller;


import org.mapstruct.control.MappingControl.Use;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.codewithmosh.store.dto.LoginUserDto;
import com.codewithmosh.store.dto.UserDto;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.dto.JwtTockenDto;
import com.codewithmosh.store.service.JwtService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/login")
@AllArgsConstructor
public class AuthController {

    private AuthenticationManager authenticationManager;

    private JwtService jwtService;
    private UserRepository userRepository;
    private UserMapper userMapper;


    


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

    public ResponseEntity<UserDto> me(){
      var authenticate = SecurityContextHolder.getContext().getAuthentication();
      var email = (String) authenticate.getPrincipal();

      var user = userRepository.findByEmail(email).orElse(null);

      if (user ==null){
        return ResponseEntity.notFound().build();
      }

      var userDto = userMapper.toDto(user);

      return ResponseEntity.ok().body(userDto);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException(){

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }
 
    
}
