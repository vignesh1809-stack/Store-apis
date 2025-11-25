package com.codewithmosh.store.controller;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.codewithmosh.store.dto.ErrorDto;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleUnreadableException(){

        var errorDto = new ErrorDto("Invalid Input");
        return ResponseEntity.badRequest().body(errorDto);

    }


    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HashMap<String,String>> handleValidationError(
        MethodArgumentNotValidException exception
    ){

        var errors = new HashMap<String,String>();

        exception.getBindingResult().getFieldErrors().forEach( error ->
        
            errors.put(error.getField(),error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);

    }

    
}
