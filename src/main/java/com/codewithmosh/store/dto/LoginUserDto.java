package com.codewithmosh.store.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginUserDto {


    @NotBlank
    @Email
    private String Email;

    @NotBlank
    @Size(min=4)
    private String Password;
    
}
