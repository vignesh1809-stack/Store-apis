package com.codewithmosh.store.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewithmosh.store.dto.GetOrderDto;
import com.codewithmosh.store.exception.NoOrdersForTheUserException;
import com.codewithmosh.store.exception.OrderNotFoundException;
import com.codewithmosh.store.service.OrderService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {


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

