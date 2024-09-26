package com.la.javaweb.service;

import com.la.javaweb.model.Comment;
import com.la.javaweb.model.Product;
import com.la.javaweb.util.exception.AppException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IProductService {
    Page<Product> getAllProducts(Pageable pageable);
    Page<Product> searchProductsByName(String productName, Pageable pageable);
    Optional<Product> getProductById(Long productId);
    Page<Product> getLatestProducts(Pageable pageable);
    Page<Product> getProductsByCategoryId(Long categoryId, Pageable pageable);

    void addProduct(Product product, List<MultipartFile> images) throws AppException;
    void updateProduct(Long productId, Product updatedProduct, List<MultipartFile> newImages) throws AppException;
    boolean deleteProduct(Long productId);
    boolean existsProductById(Long productId);
    List<Comment> getCommentsByProductId(Long productId);
    Page<Product> getBestSellingProducts(int month, int year, int limit);

}
