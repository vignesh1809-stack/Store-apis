package com.codewithmosh.store.service;

import com.codewithmosh.store.entities.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResult {
    private Long userId;
    private Status paymentStatus;

    
}
