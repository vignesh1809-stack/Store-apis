package com.codewithmosh.store.service;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.codewithmosh.store.dto.CheckoutRequestDto;
import com.codewithmosh.store.dto.OrderDto;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.exception.CartItemsNotFoundException;
import com.codewithmosh.store.exception.CartNotFoundException;
import com.codewithmosh.store.exception.PaymentException;
import com.codewithmosh.store.exception.UnAuthorizedUserException;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.repositories.OrderRepository;
import com.codewithmosh.store.repositories.UserRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;

    @Value("${websiteUrl}")
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

        try{
            
            var session = paymentGateway.createCheckoutSession(order);
            cartService.clearCartService(cartId);
     

            return new OrderDto(order.getId(),session.getUrl());

        }catch(PaymentException ex){
            System.out.println(ex);
            orderRepository.delete(order);
            throw ex;

        }
    }

    public  void  handleWebhook(WebhookRequest request){

        paymentGateway.parseWebhookRequest(request).ifPresent(
            paymentResult ->{
                var order = orderRepository.findById(Long.valueOf(paymentResult.getUserId())).orElseThrow();
                    order.setStatus(paymentResult.getPaymentStatus());
                    orderRepository.save(order);

            }
        );

         
     }

       
    

    
}
