package com.codewithmosh.store.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.OrderItems;
import com.codewithmosh.store.entities.Status;
import com.codewithmosh.store.exception.PaymentException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;


@Service
public class StripePaymentService  implements PaymentGateway{

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Value("${webhookSecretKey}")
    private String webhookSecretKey;


    @Override
    public CheckoutSession createCheckoutSession(Order order){
        
        try{
        var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "checkout-success?orderId=" + order.getId())
                    .setCancelUrl(websiteUrl + "checkout-failure")
                    .putMetadata("order_Id", order.getId().toString());

            order.getOrderItems().forEach(item -> {
                var lineItem = getLineItem(item);

                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build());

            return new CheckoutSession(session.getUrl());
             

        }catch(StripeException ex){
            System.out.println(ex);
            throw new PaymentException();

        }
        
    }

    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request){

        var signature = request.getHeader().get("stripe-signature");
        var payload = request.getPayload();


            try {
            var event = Webhook.constructEvent(payload, signature, webhookSecretKey);
            var orderId = extractOrderId(event);
          
            
            return switch(event.getType()) {
                case "checkout.session.completed", "payment_intent.succeeded", "charge.succeeded" ->
                    Optional.of(new PaymentResult(orderId, Status.PAID));
                    
                case "payment_intent.payment_failed", "charge.failed" ->
                    Optional.of(new PaymentResult(orderId, Status.FAILED));
                    
                default ->
                    Optional.empty();
            };

        } catch (SignatureVerificationException e) {
            throw new PaymentException();
        }
   
    }



    private Long extractOrderId(Event event){
        var stripeObject = event.getDataObjectDeserializer().getObject().orElseThrow();
        String orderId = null;
        
        // Handle different object types based on event type
        if (stripeObject instanceof Session session) {
            // For checkout.session.completed events
            orderId = session.getMetadata().get("order_Id");
        } else if (stripeObject instanceof PaymentIntent paymentIntent) {
            // For payment_intent events - try to get from metadata or retrieve session
            orderId = paymentIntent.getMetadata().get("order_Id");
            if (orderId == null && paymentIntent.getMetadata().containsKey("checkout_session_id")) {
                try {
                    var sessionId = paymentIntent.getMetadata().get("checkout_session_id");
                    var session = Session.retrieve(sessionId);
                    orderId = session.getMetadata().get("order_Id");
                } catch (StripeException e) {
                    // Fallback: try to get from payment intent metadata directly
                }
            }
        } else if (stripeObject instanceof Charge charge) {
            // For charge events - get payment intent first, then session
            try {
                var paymentIntentId = charge.getPaymentIntent();
                if (paymentIntentId != null) {
                    var paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                    orderId = paymentIntent.getMetadata().get("order_Id");
                    if (orderId == null && paymentIntent.getMetadata().containsKey("checkout_session_id")) {
                        var sessionId = paymentIntent.getMetadata().get("checkout_session_id");
                        var session = Session.retrieve(sessionId);
                        orderId = session.getMetadata().get("order_Id");
                    }
                }
            } catch (StripeException e) {
                throw new PaymentException();
            }
        }
        
        if (orderId == null) {
            throw new PaymentException();
        }
        
        return Long.valueOf(orderId);
    }

    private LineItem getLineItem(OrderItems item) {
        return SessionCreateParams.LineItem.builder()
            .setQuantity((long) item.getQuantity())
            .setPriceData(
                createPriceData(item)
            )
            .build();
    }

    private PriceData createPriceData(OrderItems item) {
        return SessionCreateParams.LineItem.PriceData.builder()
            .setCurrency("usd")
            .setUnitAmountDecimal(item.getUnitPrice()
                .multiply(BigDecimal.valueOf(100)))
            .setProductData(
                createProductData(item)
            )
            .build();
    }

    private ProductData createProductData(OrderItems item) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
            .setName(item.getProduct().getName())
            .build();
    }

    
}
