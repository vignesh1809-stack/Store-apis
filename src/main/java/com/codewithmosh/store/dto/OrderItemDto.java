package com.codewithmosh.store.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long orderId;
    private CartProductDto product;
    private Long quantity;
    private BigDecimal totalPrice;
    private BigDecimal unitPrice;
}
