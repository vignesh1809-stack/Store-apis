package com.codewithmosh.store.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.codewithmosh.store.dto.CheckoutRequestDto;
import com.codewithmosh.store.dto.OrderDto;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.exception.CartItemsNotFoundException;
import com.codewithmosh.store.exception.CartNotFoundException;
import com.codewithmosh.store.exception.UnAuthorizedUserException;
import com.codewithmosh.store.mappers.OrderMapper;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import com.codewithmosh.store.repositories.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;



import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;

    @Value("")
    private String websiteUrl;

    public  OrderDto  createOrderService(CheckoutRequestDto request) throws Exception{

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

        //Create checkout session
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "checkout-success?orderId=" + order.getId())
                    .setCancelUrl(websiteUrl + "checkout-failure");

            order.getOrderItems().forEach(item -> {
                var lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) item.getQuantity())
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("usd")
                            .setUnitAmountDecimal(item.getUnitPrice())
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(item.getProduct().getName())
                                    .build()
                            )
                            .build()
                    )
                    .build();

                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build());


       
        cartService.clearCartService(cartId);
     

        return new OrderDto(order.getId(),session.getUrl());
        
    }

    
}
