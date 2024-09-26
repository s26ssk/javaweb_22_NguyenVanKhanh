package com.la.javaweb.service;

import com.la.javaweb.model.Category;
import com.la.javaweb.util.exception.AppException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {
    List<Category> getAllCategories();
    Page<Category> getAllCategories(Pageable pageable);
    Optional<Category> getCategoryById(Long categoryId);
    void addCategory(Category category) throws AppException;
    boolean updateCategory(Long categoryId, Category updatedCategory);
    boolean deleteCategory(Long categoryId);

}
