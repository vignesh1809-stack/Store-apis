package com.codewithmosh.store.service;


import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.codewithmosh.store.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService  implements UserDetailsService  {

    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String Email) throws UsernameNotFoundException{


        var user = userRepository.findByEmail(Email)
                    .orElseThrow(()-> new UsernameNotFoundException("Email Not Found"));


        return new User(
            user.getEmail(),
            user.getPassword(),
            Collections.emptyList()
        );

    }

    

    
}
