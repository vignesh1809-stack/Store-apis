package com.codewithmosh.store.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class updatingQuantityDto {

    @NotNull
    private Integer quantity;
    
}
