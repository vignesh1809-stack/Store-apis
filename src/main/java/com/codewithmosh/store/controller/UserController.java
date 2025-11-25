package com.codewithmosh.store.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.codewithmosh.store.dto.CreateUserRequest;
import com.codewithmosh.store.dto.UpdateUserRequest;
import com.codewithmosh.store.dto.UserDto;
import com.codewithmosh.store.entities.Role;
import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("users")
public class UserController {


    private final UserRepository userRepository;

    private final  UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UserDto> getAllUsers(){

        return userRepository.findAll()
                            .stream()
                            .map(userMapper::todto)
                            .collect(Collectors.toList());

    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id){

        var user = userRepository.findById(id).orElse(null);

        if (user == null){
            return ResponseEntity.notFound().build();
        }

        var userDto = userMapper.todto(user);

        return ResponseEntity.ok().body(userDto);

    }

    @PostMapping()
    //Method Arguement Exception
    public ResponseEntity<?>  createUser(
        @Valid @RequestBody CreateUserRequest request,
        UriComponentsBuilder uriBuilder
    ){

        if(userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity.badRequest().body(
                Map.of("Email", "Already exsist.")
            );

        }

        var user=userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);

        var userDto =userMapper.todto(user);

        var uri=uriBuilder
            .path("/users/{id}")
            .buildAndExpand(user.getId())
            .toUri();

        return ResponseEntity.created(uri).body(userDto);

    }


    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
        @RequestBody UpdateUserRequest request,
        @PathVariable Long id
    ){

        var user = userRepository.findById(id).orElse(null);

        if (user == null){
            return ResponseEntity.notFound().build();
        }

        userMapper.updateUser(request, user);

        var updatedUser = userRepository.save(user);

        return ResponseEntity.ok(userMapper.todto(updatedUser));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
        @PathVariable Long id

    ){
         var user = userRepository.findById(id).orElse(null);

        if (user == null){
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);

        return ResponseEntity.noContent().build();


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
