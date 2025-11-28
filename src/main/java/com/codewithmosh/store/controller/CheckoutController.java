package com.codewithmosh.store.controller;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codewithmosh.store.dto.CheckoutRequestDto;
import com.codewithmosh.store.dto.ErrorDto;
import com.codewithmosh.store.exception.CartItemsNotFoundException;
import com.codewithmosh.store.exception.CartNotFoundException;
import com.codewithmosh.store.exception.PaymentException;
import com.codewithmosh.store.exception.UnAuthorizedUserException;
import com.codewithmosh.store.service.CheckoutService;
import com.codewithmosh.store.service.WebhookRequest;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<?> createOrder (
        @RequestBody CheckoutRequestDto request
     )  throws Exception {
        
            var OrderDto = checkoutService.createOrderService(request);
            return ResponseEntity.ok().body(OrderDto);

    }

    @PostMapping("/webhook")
    public void handleWebhook(
        @RequestHeader Map<String,String> header,
        @RequestBody String payload
    ){
        
        checkoutService.handleWebhook(new WebhookRequest( header ,payload));

    }   

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException(PaymentException ex){
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDto("Error creating checkout Service"));
    }

    @ExceptionHandler(UnAuthorizedUserException.class)
    public ResponseEntity<?> handleUnauthorized(UnAuthorizedUserException ex) {
        return ResponseEntity.status(401).body(Map.of("error", ex.getMessage()));
        }

         @ExceptionHandler(CartItemsNotFoundException.class)
    public  ResponseEntity<Map<String,String>> handleCartItemsNotFound(){
        return ResponseEntity.badRequest().body(Map.of("error","Cart Items Not Found"));

    }
    @ExceptionHandler(CartNotFoundException.class)
     public ResponseEntity<Map<String,String>> handleCartNotFound(){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("ERROR","CART NOT FOUND"));
     }

    
}
