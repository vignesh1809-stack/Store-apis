package com.codewithmosh.store.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.codewithmosh.store.dto.CartDto;
import com.codewithmosh.store.dto.CartItemDto;
import com.codewithmosh.store.entities.Cart;
import com.codewithmosh.store.entities.CartItem;


@Mapper(componentModel="spring")
public interface CartMapper {
    @Mapping(target = "price" , expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);


    @Mapping(
        target = "totalPrice",
        expression = "java( cartItems.getProduct().getPrice().multiply( new java.math.BigDecimal(cartItems.getQuantity()) ) )"
    )
    CartItemDto toDto(CartItem cartItems);
    
}
