package com.codewithmosh.store.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.codewithmosh.store.dto.CartDto;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.mappers.CartMapper;
import com.codewithmosh.store.repositories.CartRepository;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {

    private CartRepository cartRepository;

    private CartMapper cartMapper;

    @PostMapping
    public ResponseEntity<CartDto> createCart(
        UriComponentsBuilder uriBuilder
    ){

        var cart = new Cart();

        cartRepository.save(cart);

        var CartDto = cartMapper.toDto(cart);

        var uri = uriBuilder.path("products/{id}").buildAndExpand(CartDto.getId()).toUri();


        return ResponseEntity.created(uri).body(CartDto);







        
    }


    
}
