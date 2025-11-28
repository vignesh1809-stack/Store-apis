package com.codewithmosh.store.service;

import java.util.Optional;

import com.codewithmosh.store.entities.Order;


public interface PaymentGateway {

     CheckoutSession createCheckoutSession(Order order);

     Optional<PaymentResult> parseWebhookRequest(WebhookRequest request);

}
