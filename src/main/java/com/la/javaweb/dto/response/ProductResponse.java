package com.la.javaweb.dto.response;

import com.la.javaweb.model.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductResponse {
    private Long productId;
    private String productName;

    private String description;

    private Double exportPrice;

    private Integer stockQuantity;
    private String categoryName;
    private Set<ProductImage> images;

    private Date createdAt = new Date();
}
