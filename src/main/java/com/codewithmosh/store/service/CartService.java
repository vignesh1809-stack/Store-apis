package com.codewithmosh.store.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;


import com.codewithmosh.store.dto.CartDto;
import com.codewithmosh.store.dto.CartItemDto;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.exception.CartItemNotFoundException;
import com.codewithmosh.store.exception.CartNotFoundException;
import com.codewithmosh.store.exception.ProductNotFoundException;
import com.codewithmosh.store.mappers.CartMapper;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private  final CartMapper cartMapper;

    private final  ProductRepository  productRepository;


    public CartDto createCartService(){
        var cart = new Cart();
        cartRepository.save(cart);
        var CartDto = cartMapper.toDto(cart);
        return CartDto;
      
    }


    public CartItemDto addCartItemService(
         @PathVariable UUID id,
          Long product_id
    ){

        var cart=cartRepository.findById(id).orElse(null);

        if (cart == null){
           throw new CartNotFoundException();
        }


        var product = productRepository.findById(product_id).orElse(null);

        if (product == null){
           throw new ProductNotFoundException();
        }

        var cartItem = cart.getCartItems(product_id);

        if (cartItem != null){

            cartItem.setQuantity(cartItem.getQuantity()+1);  

        }else{
          cart.addCartItem(product);
        }

        cartRepository.save(cart);

        var CartItemDto = cartMapper.toDto(cartItem);

        return CartItemDto;


    }

    public CartDto getAllCartItemsService(
        UUID id
    ){
        var cart = cartRepository.findById(id).orElse(null);

        if (cart == null){
            throw new CartNotFoundException();
        }

        var cartDto = cartMapper.toDto(cart);

        return cartDto;
    }


    public CartItemDto updateCartItemsService(
        UUID cart_id,
        int Quantity,
        Long product_id

    ){

        var cart = cartRepository.findById(cart_id).orElse(null);

        if (cart == null){
            throw new CartNotFoundException();
        }

        var cartItem = cart.getCartItems(product_id);
        
        if (cartItem == null){
            throw new CartItemNotFoundException();
        }


        cartItem.setQuantity(Quantity);

        cartRepository.save(cart);

        var cartItemDto = cartMapper.toDto(cartItem);

        return cartItemDto;
    }

    public CartDto removeCartItemService(
        UUID cartId,
        Long productId

    ){

        var cart = cartRepository.findById(cartId).orElse(null);

        if (cart == null){
            throw new CartNotFoundException();
        }

        var cartItem = cart.getCartItems(productId);

        if (cartItem != null){
            cart.getItems().remove(cartItem);
        }

        cartRepository.save(cart);

        var cartDto = cartMapper.toDto(cart);

        return cartDto;

    }

    public void clearCartService(
        UUID cartId
    ){
        var cart = cartRepository.findById(cartId).orElse(null);

        if (cart == null){
            throw new CartNotFoundException();
        }

        cartRepository.deleteById(cartId);


    }






    
    
}
