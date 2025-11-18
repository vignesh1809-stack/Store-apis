package com.codewithmosh.store.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class CartDto {

    private UUID id;

    private List<CartItemDto> Items= new ArrayList<> ();

    private BigDecimal price = BigDecimal.ZERO;
}
