package com.codewithmosh.store.filters;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

        if (authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        var tocken = authHeader.replace("Bearer ", "");

        var Jwt = jwtService.parseTocken(tocken);
        if (Jwt == null || Jwt.isExpired()){
            filterChain.doFilter(request, response);
            return;
            
        }
        String path = request.getRequestURI();

        if (path.equals("/checkout/webhook") || path.startsWith("/checkout/webhook")) {
             filterChain.doFilter(request, response);
                return;
        }


        
        var authentication = new UsernamePasswordAuthenticationToken(
            Jwt.getId(),
            null,
             List.of( new SimpleGrantedAuthority("ROLE_"+ Jwt.getRole()))
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);


        filterChain.doFilter(request, response);

        
    }

    
}
