package com.codewithmosh.store.entities;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

@Table(name ="Carts")
public class Cart {

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    @Column(name="id" , nullable = false)
    private UUID id;

    @Column(name="date_created",updatable = false,nullable = false )
    @CreationTimestamp
    private Date date_created;

    
}
