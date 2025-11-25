package com.codewithmosh.store.controller;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewithmosh.store.dto.CheckoutRequestDto;
import com.codewithmosh.store.dto.OrderDto;
import com.codewithmosh.store.exception.CartItemsNotFoundException;
import com.codewithmosh.store.exception.UnAuthorizedUserException;

import com.codewithmosh.store.service.CheckoutService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(("/checkout"))
@AllArgsConstructor
public class CheckoutController {

    private CheckoutService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
        @RequestBody CheckoutRequestDto request
    )throws Exception {
        

        var OrderDto = orderService.createOrderService(request);
        return ResponseEntity.ok().body(OrderDto);


    }

    @ExceptionHandler(UnAuthorizedUserException.class)
    public ResponseEntity<?> handleUnauthorized(UnAuthorizedUserException ex) {
        return ResponseEntity.status(401).body(Map.of("error", ex.getMessage()));
        }

         @ExceptionHandler(CartItemsNotFoundException.class)
    public  ResponseEntity<Map<String,String>> handleCartItemsNotFound(){
        return ResponseEntity.badRequest().body(Map.of("error","Cart Items Not Found"));

    }

    
}
