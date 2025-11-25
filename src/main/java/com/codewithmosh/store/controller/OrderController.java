package com.codewithmosh.store.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewithmosh.store.dto.GetOrderDto;
import com.codewithmosh.store.exception.CartItemsNotFoundException;
import com.codewithmosh.store.exception.NoOrdersForTheUserException;
import com.codewithmosh.store.exception.OrderNotFoundException;
import com.codewithmosh.store.mappers.OrderMapper;
import com.codewithmosh.store.repositories.OrderRepository;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.service.AuthService;
import com.codewithmosh.store.service.JwtService;
import com.codewithmosh.store.service.OrderService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final JwtService  jwtService;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final OrderService orderService;

    @GetMapping
    public List<GetOrderDto> getAllOrders(){

        return orderService.getAllOrdersService();
        
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetOrderDto> getOrderById(
        @PathVariable Long id
    ){
        var getOrderDto= orderService.getOrderByIdService(id);
        return ResponseEntity.ok().body(getOrderDto);
    }
    @ExceptionHandler(OrderNotFoundException.class)
    public  ResponseEntity<Map<String,String>> handleOrderNotFoundException(){
        return ResponseEntity.badRequest().body(Map.of("error","order Not Found"));

    }

    @ExceptionHandler(NoOrdersForTheUserException.class)
    public  ResponseEntity<Map<String,String>> handleNoOrdersForTheUserException(){
        return ResponseEntity.badRequest().body(Map.of("error","No Orders For The User"));

    }
    
}

