package com.codewithmosh.store.mappers;

import org.mapstruct.Mapper;

import com.codewithmosh.store.dto.CartDto;
import com.codewithmosh.store.entities.Cart;

@Mapper(componentModel="spring")
public interface CartMapper {

    CartDto toDto(Cart cart);
    
}
