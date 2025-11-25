package com.codewithmosh.store.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequestDto {

    @NotNull
    private UUID cartId;

    
}
