package com.codewithmosh.store.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class OrderItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name ="orderId" , referencedColumnName = "id")
    private Order order;

    @ManyToOne
    @JoinColumn(name ="productId", referencedColumnName = "id")
    private Product product;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    private Integer quantity;

    @Column(name ="total_price")
    private BigDecimal totalPrice;

    public  OrderItems(Order order, Product product ,Integer quantity) {
        this.order=order;
        this.product=product;
        this.quantity=quantity;
        this.unitPrice=product.getPrice();
        this.totalPrice=unitPrice.multiply(BigDecimal.valueOf(quantity));
  };

}
