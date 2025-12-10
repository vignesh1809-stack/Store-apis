package com.codewithmosh.store.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.codewithmosh.store.dto.LoginUserDto;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.dto.JwtTockenDto;
import com.codewithmosh.store.service.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/login")
@AllArgsConstructor
public class AuthController {

    private AuthenticationManager authenticationManager;

    private JwtService jwtService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private JwtConfig jwtConfig;


  


    @PostMapping
    public ResponseEntity<JwtTockenDto> loginUser(
        @Valid@RequestBody LoginUserDto request,
        HttpServletResponse response
    ){ 

      authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
      );

      var user = userRepository.findByEmail(request.getEmail()).orElse(null);

      if (user == null){
        return ResponseEntity.notFound().build();
      }

      var jwttocken = jwtService.generateJwtTocken(user);
      var refreshTocken = jwtService.genrerateRefreshTocken(user);

      var cookie = new Cookie("refreshTocken" ,refreshTocken.toString());
      cookie.setHttpOnly(true);
      cookie.setPath("login/refresh");
      cookie.setMaxAge(jwtConfig.getRefreshTockenExpiration());
      cookie.setSecure(true);

      response.addCookie(cookie);


        
      return ResponseEntity.ok(new JwtTockenDto(jwttocken.toString()));

    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtTockenDto> refresh(
      @CookieValue(value = "refreshTocken") String refreshTocken
    ){
      var Jwt = jwtService.parseTocken(refreshTocken);
        if (Jwt == null || Jwt.isExpired()){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      var userId = Jwt.getId();
      var user = userRepository.findById(userId).orElseThrow();
      var accessTocken= jwtService.generateJwtTocken(user);
      

      return ResponseEntity.ok().body(new JwtTockenDto(accessTocken.toString()));


    }
    

    @GetMapping("/me")
    public ResponseEntity<?> me(){
      var authenticate = SecurityContextHolder.getContext().getAuthentication();
      var principal = authenticate.getPrincipal();
      
      if (principal == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("not found");
      }
      
      var Id = (Long) principal;


      var user = userRepository.findById(Id).orElse(null);

      if (user == null){
        return ResponseEntity.notFound().build();
      }

      var userDto = userMapper.todto(user);

      return ResponseEntity.ok().body(userDto);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException(){

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }
 
    
}
