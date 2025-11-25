package com.codewithmosh.store.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class GetOrderItemDto {
    private CartProductDto product;
    private Long quantity;
    private BigDecimal totalPrice;
    
}
