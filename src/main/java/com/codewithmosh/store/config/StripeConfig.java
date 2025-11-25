package com.codewithmosh.store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {

    @Value("${stripe.secretKey}")
    private String key;

    @PostConstruct
    public void init(){
        Stripe.apiKey = key;
    }
    
}
