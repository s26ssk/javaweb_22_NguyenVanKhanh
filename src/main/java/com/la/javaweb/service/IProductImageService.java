package com.la.javaweb.service;

import com.la.javaweb.model.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductImageService {
    List<ProductImage> saveProductImages(Long productId, List<MultipartFile> images);
    void deleteProductImagesByProductId(Long productId);
}
