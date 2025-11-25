package com.codewithmosh.store.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import com.codewithmosh.store.entities.Status;

import lombok.Data;

@Data
public class GetOrderDto {

    private Long id;
    private Status status;
    private LocalDateTime created_at;
    private List<GetOrderItemDto> orderItems = new ArrayList<>();
    private BigDecimal totalPrice;
    
}
