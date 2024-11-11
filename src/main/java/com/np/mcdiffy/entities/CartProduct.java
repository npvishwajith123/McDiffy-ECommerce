package com.np.mcdiffy.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cpId;

    @ManyToOne(fetch = FetchType.EAGER,
               cascade = {CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "cartId")
    @JsonBackReference
    private Cart cart;

    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "productId")
    private Product product;
    private int quantity;

    @Override
    public String toString() {
        return "CartProduct{" +
                "cpId=" + cpId +
                ", quantity=" + quantity +
                '}';
    }
}
