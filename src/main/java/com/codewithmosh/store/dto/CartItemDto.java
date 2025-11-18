package com.codewithmosh.store.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class CartItemDto {

    private CartProductDto product;
    private Long quantity;
    private BigDecimal totalPrice;

}
