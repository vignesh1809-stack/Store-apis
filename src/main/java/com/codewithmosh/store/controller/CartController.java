package com.codewithmosh.store.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;
import java.util.UUID;

import com.codewithmosh.store.dto.AddCartItemsDto;
import com.codewithmosh.store.dto.CartDto;
import com.codewithmosh.store.dto.CartItemDto;
import com.codewithmosh.store.dto.updatingQuantityDto;
import com.codewithmosh.store.exception.ProductNotFoundException;
import com.codewithmosh.store.exception.CartNotFoundException;
import com.codewithmosh.store.exception.CartItemNotFoundException;

import com.codewithmosh.store.service.CartService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {


    private CartService cartService;




    @PostMapping
    public ResponseEntity<CartDto> createCart(
        UriComponentsBuilder uriBuilder
    ){

        var CartDto = cartService.createCartService();

        var uri = uriBuilder.path("products/{id}").buildAndExpand(CartDto.getId()).toUri();


        return ResponseEntity.created(uri).body(CartDto);
        
    }


    @PostMapping("/{id}/items")
    public ResponseEntity<CartItemDto> addCartItem(
        @PathVariable UUID id,
        @RequestBody AddCartItemsDto request
    ){

        var cartItemDto = cartService.addCartItemService(id,request.getProductId());
        
        return ResponseEntity.ok().body(cartItemDto); 
    }


    @GetMapping("/{id}")
    public ResponseEntity<CartDto> getAllCartItems(
        @PathVariable UUID id
    ){
        var cartDto=cartService.getAllCartItemsService(id);

        return ResponseEntity.ok().body(cartDto);

    }


    @PutMapping("{cart_id}/items/{product_id}")
    public ResponseEntity<CartItemDto> updateCartItems(
        @PathVariable UUID cart_id,
        @PathVariable Long product_id,
        @Valid @RequestBody updatingQuantityDto request
    ){
        var cartItemDto= cartService.updateCartItemsService(cart_id, request.getQuantity(), product_id);

        return ResponseEntity.ok().body(cartItemDto);

        }


    @DeleteMapping("{cartId}/items/{productId}")
     public ResponseEntity<CartDto> removeCartItem(
        @PathVariable UUID cartId,
        @PathVariable Long productId
     ){


        var cartDto = cartService.removeCartItemService(cartId, productId);

        return ResponseEntity.ok().body(cartDto);


     }

     @DeleteMapping("{cartId}/items")
     public ResponseEntity<?> clearCart(
        @PathVariable UUID cartId
     ){
        cartService.clearCartService(cartId);
         
        return ResponseEntity.ok().build();


     }

     @ExceptionHandler(CartNotFoundException.class)
     public ResponseEntity<Map<String,String>> handleCartNotFound(){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("ERROR","CART NOT FOUND"));
     }

     @ExceptionHandler(ProductNotFoundException.class)
     public ResponseEntity<Map<String,String>> handleProductNotFound(){

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("ERROR","CART NOT FOUND"));
     }


     @ExceptionHandler(CartItemNotFoundException.class)
     public ResponseEntity<Map<String,String>> handleCartItemNotFound(){

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("ERROR","CART ITEM NOT FOUND"));
     }

        
    }


    

