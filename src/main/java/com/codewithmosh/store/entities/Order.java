package com.codewithmosh.store.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CurrentTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="orders")
public class Order {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @ManyToOne
    @JoinColumn(name ="customerId" ,referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CurrentTimestamp
    private LocalDateTime created_at;

    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItems> OrderItems = new ArrayList<>();



    public static  Order fromCart(Cart cart,User user){
        var order = new Order();
        order.setUser(user);
        order.setTotalPrice(cart.getTotalPrice());  
        order.setStatus(Status.PENDING);

        cart.getItems().forEach(Item ->{
                   var orderItem = new OrderItems(order,Item.getProduct(),Item.getQuantity());
                    order.getOrderItems().add(orderItem);
        });

        return order;



    }

}
