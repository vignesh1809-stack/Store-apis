package com.codewithmosh.store.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.codewithmosh.store.dto.GetOrderDto;
import com.codewithmosh.store.exception.NoOrdersForTheUserException;
import com.codewithmosh.store.exception.OrderNotFoundException;
import com.codewithmosh.store.mappers.OrderMapper;
import com.codewithmosh.store.repositories.OrderRepository;
import com.codewithmosh.store.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor 
public class OrderService {
    
    private final  AuthService authService;
    private UserRepository userRepository;
    private OrderRepository orderRepository;
    private OrderMapper orderMapper;

    public List<GetOrderDto> getAllOrdersService(){
        var userId = authService.getUserId();
        var user =  userRepository.findById(userId);

        return orderRepository.findAllByUser(user).stream()
                    .map(orderMapper::toGetOrderDto)
                    .collect(Collectors.toList());
    }

    public GetOrderDto getOrderByIdService(Long id){
        var order = orderRepository.findById(id).orElse(null);
        var userId = authService.getUserId();

        if (order == null){
            throw new OrderNotFoundException();
        }
        if (!order.getUser().getId().equals(userId) ){
            throw new NoOrdersForTheUserException();
        }

        var GetOrderDto = orderMapper.toGetOrderDto(order);

        return GetOrderDto;

    }

    
}
