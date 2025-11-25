package com.codewithmosh.store.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.User;

public interface OrderRepository  extends JpaRepository<Order,Long>{

    List<Order> findAllByUser(Optional<User> user);
    
}
