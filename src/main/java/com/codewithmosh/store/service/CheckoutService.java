package com.codewithmosh.store.service;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.codewithmosh.store.dto.CheckoutRequestDto;
import com.codewithmosh.store.dto.OrderDto;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.exception.CartItemNotFoundException;
import com.codewithmosh.store.exception.CartItemsNotFoundException;
import com.codewithmosh.store.exception.CartNotFoundException;
import com.codewithmosh.store.exception.UnAuthorizedUserException;
import com.codewithmosh.store.mappers.OrderMapper;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import com.codewithmosh.store.repositories.UserRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
public class CheckoutService {

    private final AuthService authService;
    private UserRepository userRepository;
    private CartRepository cartRepository;
    private OrderRepository orderRepository;
    private OrderMapper orderMapper;
    private CartService cartService;




    public  OrderDto  createOrderService(CheckoutRequestDto request){

        var userId = authService.getUserId();
        var user = userRepository.findById(userId).orElse(null);

        if (userId == null) {
        throw new UnAuthorizedUserException("User not authorized");
        }


        var cartId = request.getCartId();
        var cart= cartRepository.findById(cartId).orElse(null); 
        if (cart == null){
           throw new CartNotFoundException();
        }
        if (cart.getItems().isEmpty()){
           throw new CartItemsNotFoundException();
        }



        var order = Order.fromCart(cart,user);
        orderRepository.save(order);
        var orderDto = orderMapper.toDto(order);
        cartService.clearCartService(cartId);

        return orderDto;
        
    }
    
}
