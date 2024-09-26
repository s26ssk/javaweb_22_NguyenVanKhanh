package com.la.javaweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    private String description;

    private Double importPrice;
    private Double exportPrice;

    private Integer stockQuantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<ProductImage> images;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "categoryId", referencedColumnName = "categoryId")
    private Category category;

    private Date createdAt = new Date();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<WishList> wishList;

}
