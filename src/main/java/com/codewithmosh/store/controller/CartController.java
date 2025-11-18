package com.codewithmosh.store.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;
import java.util.UUID;

import com.codewithmosh.store.dto.AddCartItemsDto;
import com.codewithmosh.store.dto.CartDto;
import com.codewithmosh.store.dto.CartItemDto;
import com.codewithmosh.store.dto.updatingQuantityDto;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.entities.CartItem;
import com.codewithmosh.store.mappers.CartMapper;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {

    private CartRepository cartRepository;

    private CartMapper cartMapper;

    private final ProductRepository productRepository;

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


    @PostMapping("/{id}/items")
    public ResponseEntity<CartItemDto> addCartItem(
        @PathVariable UUID id,
        @RequestBody AddCartItemsDto request
    ){

        var cart=cartRepository.findById(id).orElse(null);

        if (cart == null){
           return ResponseEntity.notFound().build();
        }


        var product = productRepository.findById(request.getProductId()).orElse(null);

        if (product == null){
           return ResponseEntity.notFound().build();
        }

        var cartItem = cart.getCartItems(request.getProductId());

        if (cartItem != null){

            cartItem.setQuantity(cartItem.getQuantity()+1);  

        }else{
          cart.addCartItem(product);
        }

        cartRepository.save(cart);

        var CartItemDto = cartMapper.toDto(cartItem);
        
        return ResponseEntity.ok().body(CartItemDto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CartDto> getAllCartItems(
        @PathVariable UUID id
    ){
        var cart = cartRepository.findById(id).orElse(null);

        if (cart == null){
            return ResponseEntity.notFound().build();
        }

        var cartDto = cartMapper.toDto(cart);

        return ResponseEntity.ok().body(cartDto);

    }

    @PutMapping("{cart_id}/items/{product_id}")
    public ResponseEntity<?> updateCartItems(
        @PathVariable UUID cart_id,
        @PathVariable Long product_id,
        @Valid @RequestBody updatingQuantityDto request
    ){
        var cart = cartRepository.findById(cart_id).orElse(null);

        if (cart == null){
            return ResponseEntity.notFound().build();
        }

        var cartItem = cart.getCartItems(product_id);
        
        if (cartItem == null){
            return ResponseEntity.notFound().build();
        }

        cartItem.setQuantity(request.getQuantity());

        cartRepository.save(cart);

        return ResponseEntity.ok().body(cartMapper.toDto(cartItem));

        }

    @DeleteMapping("{cartId}/items/{productId}")
     public ResponseEntity<?> removeCartItem(
        @PathVariable UUID cartId,
        @PathVariable Long productId
     ){

        var cart = cartRepository.findById(cartId).orElse(null);

        if (cart == null){
            return ResponseEntity.notFound().build();
        }

        var cartItem = cart.getCartItems(productId);

        if (cartItem != null){
            cart.getItems().remove(cartItem);
        }

        cartRepository.save(cart);

        var cartDto = cartMapper.toDto(cart);

        return ResponseEntity.ok().body(cartDto);


     }

     @DeleteMapping("{cartId}/items")
     public ResponseEntity<?> clearCart(
        @PathVariable UUID cartId
     ){
        var cart = cartRepository.findById(cartId).orElse(null);

        if (cart == null){
            return ResponseEntity.notFound().build();
        }

        

        cartRepository.deleteById(cartId);
         
        return ResponseEntity.ok().build();


     }





        
    }


    

