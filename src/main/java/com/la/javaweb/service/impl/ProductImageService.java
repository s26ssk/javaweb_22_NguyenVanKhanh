package com.la.javaweb.service.impl;

import com.la.javaweb.model.Product;
import com.la.javaweb.model.ProductImage;
import com.la.javaweb.repository.IProductImageRepository;
import com.la.javaweb.security.jwt.JwtEntryPoint;
import com.la.javaweb.service.IProductImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductImageService implements IProductImageService {
    @Autowired
    private IProductImageRepository productImageRepository;
    private final Logger logger = LoggerFactory.getLogger(JwtEntryPoint.class);
    @Value("${uploadPath}")
    private String uploadPath;

    @Override
    public List<ProductImage> saveProductImages(Long productId, List<MultipartFile> images) {
        List<ProductImage> savedProductImages = new ArrayList<>();

        for (MultipartFile file : images) {
            String fileName = file.getOriginalFilename();

            try {
                File uploadFolder = new File(uploadPath);

                if (!uploadFolder.exists()) {
                    if (uploadFolder.mkdir()) {
                        logger.info("Created Upload Folder successfully");
                    } else {
                        logger.error("Failed to create Upload Folder");
                    }
                }

                FileCopyUtils.copy(file.getBytes(), new File(uploadPath  + fileName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ProductImage productImage = ProductImage.builder()
                    .product(Product.builder().productId(productId).build())
                    .productImageName(fileName)
                    .build();

            savedProductImages.add(productImageRepository.save(productImage));
        }

        return savedProductImages;
    }

    @Override
    public void deleteProductImagesByProductId(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductProductId(productId);

        for (ProductImage productImage : productImages) {
            String imageName = productImage.getProductImageName();
            String imagePath = uploadPath + imageName;

            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                if (imageFile.delete()) {
                    logger.info("Deleted image file: {}", imagePath);
                } else {
                    logger.error("Failed to delete image file: {}", imagePath);
                }
            }

            productImageRepository.delete(productImage);
        }
    }
}
