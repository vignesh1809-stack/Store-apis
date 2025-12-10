package com.codewithmosh.store.service;



import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class AuthService {

    public Long getUserId(){
     var authenticate = SecurityContextHolder.getContext().getAuthentication();
      var principal = authenticate.getPrincipal();
      
      var Id = (Long) principal;

      return Id;

    }
  
}
