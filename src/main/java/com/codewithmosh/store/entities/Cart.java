package com.codewithmosh.store.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder.Default;


@Getter
@Setter
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


    @OneToMany(mappedBy = "cart" , cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<CartItem> items = new LinkedHashSet<>();


    public  BigDecimal getTotalPrice(){
         return items.stream()
         .map(CartItem::getTotalPrice)
         .reduce(BigDecimal.ZERO,BigDecimal::add);
         
    }

    public CartItem getCartItems(Long product_id){
    return items.stream()
           .filter(item -> item.getProduct().getId().equals(product_id))
           .findFirst()
           .orElse(null);

    }

    public CartItem addCartItem(Product product){
            var cartItem = new CartItem();
            cartItem.setQuantity(1);
            cartItem.setCart(this);
            cartItem.setProduct(product);
            items.add(cartItem);

        return cartItem;
    }

    public void clear(){
        items.clear();
    }


}

