package com.la.javaweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productImageId;
    private String productImageName;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;
}
