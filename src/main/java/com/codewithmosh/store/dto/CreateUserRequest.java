package com.codewithmosh.store.dto;

import com.codewithmosh.store.validaters.Lowercase;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "name should not be null")
    @Size(min=3 , max = 255)
    private String name;

    @NotBlank
    @Email(message = "Email is invalid")
    @Lowercase
    private String email; 

    @NotBlank
    @Size(min=6,max=25,message="Email must be greater than 6 and less than 25")
    private String password;
    
}
