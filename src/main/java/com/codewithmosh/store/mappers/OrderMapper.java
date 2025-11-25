package com.codewithmosh.store.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.codewithmosh.store.entities.*;
import com.codewithmosh.store.dto.GetOrderDto;
import com.codewithmosh.store.dto.GetOrderItemDto;
import com.codewithmosh.store.dto.OrderDto;
import com.codewithmosh.store.dto.OrderItemDto;
import com.codewithmosh.store.entities.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "orderId",source = "id")
    OrderDto toDto(Order order);

    @Mapping(target = "unitPrice", source = "product.price")
    OrderItemDto toOrderItem(CartItem cartItem);

    OrderItems toDto(OrderItemDto orderItemDto);


    GetOrderDto toGetOrderDto(Order order);

    GetOrderItemDto toGetOrderItemDto(OrderItems orderItems);

    
    
}
