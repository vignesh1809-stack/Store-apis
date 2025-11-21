package com.codewithmosh.store.filters;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import com.codewithmosh.store.service.JwtService;



@Component
@AllArgsConstructor
public class JwtTockenAuthentication extends OncePerRequestFilter{


    private final JwtService jwtService ;

    @Override
    public void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response,
         FilterChain filterChain) throws IOException,ServletException{

        var authHeader = request.getHeader("Authorization");

        if (authHeader==null || !authHeader.startsWith("Barear ")){
            filterChain.doFilter(request, response);
            return;
        }

        var tocken = authHeader.replace("Barear ", "");

        if (!jwtService.validateTocken(tocken)){
            filterChain.doFilter(request, response);
            return;
            
        }

        var authentication = new UsernamePasswordAuthenticationToken(
            jwtService.getEmailFromTocken(tocken),
            null,
            null
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);


        filterChain.doFilter(request, response);




        
    }

    
}
