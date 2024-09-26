package com.la.javaweb.service.impl;

import com.la.javaweb.model.Comment;
import com.la.javaweb.model.Product;
import com.la.javaweb.model.ProductImage;
import com.la.javaweb.repository.IProductRepository;
import com.la.javaweb.service.ICommentService;
import com.la.javaweb.service.IProductImageService;
import com.la.javaweb.service.IProductService;
import com.la.javaweb.util.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements IProductService {
    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private IProductImageService productImageService;
    @Autowired
    private ICommentService commentService;

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }


    @Override
    public Page<Product> searchProductsByName(String productName, Pageable pageable) {
        return productRepository.findByProductNameContaining(productName, pageable);
    }

    @Override
    public Optional<Product> getProductById(Long productId) {
        return productRepository.findByProductId(productId);
    }

    @Override
    public Page<Product> getLatestProducts(Pageable pageable) {
        return productRepository.findTop5ByOrderByCreatedAtDesc(pageable);
    }

    @Override
    public Page<Product> getProductsByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryCategoryId(categoryId, pageable);
    }

    @Override
    public void addProduct(Product product, List<MultipartFile> images){
        Product savedProduct = productRepository.save(product);

        List<ProductImage> savedProductImages = productImageService.saveProductImages(savedProduct.getProductId(), images);

        savedProduct.setImages(new HashSet<>(savedProductImages));
        productRepository.save(savedProduct);
    }
    @Override
    public void updateProduct(Long productId, Product updatedProduct, List<MultipartFile> newImages) throws AppException {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new AppException("Product not found"));

        existingProduct.setProductName(updatedProduct.getProductName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setImportPrice(updatedProduct.getImportPrice());
        existingProduct.setExportPrice(updatedProduct.getExportPrice());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setStockQuantity(updatedProduct.getStockQuantity());

        Product savedProduct = productRepository.save(existingProduct);

        productImageService.deleteProductImagesByProductId(productId);

        List<ProductImage> newProductImages = productImageService.saveProductImages(savedProduct.getProductId(), newImages);

        savedProduct.setImages(new HashSet<>(newProductImages));
        productRepository.save(savedProduct);
    }
    @Override
    public boolean deleteProduct(Long productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
            return true;
        }
        return false;
    }

    @Override
    public boolean existsProductById(Long productId) {
        return productRepository.existsById(productId);
    }
    @Override
    public List<Comment> getCommentsByProductId(Long productId) {
        return commentService.getCommentsByProductId(productId);
    }

    @Override
    public Page<Product> getBestSellingProducts(int month, int year, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findBestSellingProducts(month, year, pageable);
    }

}
