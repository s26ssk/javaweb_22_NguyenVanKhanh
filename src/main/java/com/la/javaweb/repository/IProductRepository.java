package com.la.javaweb.repository;

import com.la.javaweb.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByProductNameContaining(String productName, Pageable pageable);
    Optional<Product> findByProductId(Long productId);
    Page<Product> findTop5ByOrderByCreatedAtDesc(Pageable pageable);
    Page<Product> findByCategoryCategoryId(Long categoryId, Pageable pageable);
    @Query("SELECT p FROM Product p " +
            "INNER JOIN OrderDetails od ON p.productId = od.product.productId " +
            "INNER JOIN Order o ON od.order.orderId = o.orderId " +
            "WHERE FUNCTION('MONTH', o.createdAt) = :month AND FUNCTION('YEAR', o.createdAt) = :year " +
            "GROUP BY p.productId " +
            "ORDER BY SUM(od.orderQuantity) DESC")
    Page<Product> findBestSellingProducts(@Param("month") int month,
                                                  @Param("year") int year,
                                                  Pageable pageable);

}
