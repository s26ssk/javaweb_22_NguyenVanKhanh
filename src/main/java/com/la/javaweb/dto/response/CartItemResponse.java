package com.la.javaweb.dto.response;

import com.la.javaweb.model.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CartItemResponse {
    private Long shoppingCartId;
    private Long productId;
    private String productName;
    private int quantity;
    private Set<ProductImage> images;
    private Double totalPriceProduct;
}
