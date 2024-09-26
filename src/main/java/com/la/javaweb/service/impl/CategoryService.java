package com.la.javaweb.service.impl;

import com.la.javaweb.model.Category;
import com.la.javaweb.repository.ICategoryRepository;
import com.la.javaweb.service.ICategoryService;
import com.la.javaweb.util.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {
    @Autowired
    private ICategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    @Override
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    @Override
    public void addCategory(Category category) throws AppException {
        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new AppException("Category with the same name already exists");
        }

        categoryRepository.save(category);
    }

    @Override
    public boolean updateCategory(Long categoryId, Category updatedCategory) {
        Optional<Category> existingCategoryOptional = categoryRepository.findById(categoryId);

        if (existingCategoryOptional.isPresent()) {
            Category existingCategory = existingCategoryOptional.get();

            if (!updatedCategory.getCategoryName().equals(existingCategory.getCategoryName())
                    && categoryRepository.existsByCategoryName(updatedCategory.getCategoryName())) {
                return false;
            }

            existingCategory.setCategoryName(updatedCategory.getCategoryName());
            existingCategory.setDescription(updatedCategory.getDescription());
            existingCategory.setStatus(updatedCategory.getStatus());

            categoryRepository.save(existingCategory);
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean deleteCategory(Long categoryId) {
        Optional<Category> existingCategoryOptional = categoryRepository.findById(categoryId);

        if (existingCategoryOptional.isPresent()) {
            categoryRepository.deleteById(categoryId);
            return true;
        } else {
            return false;
        }
    }
}
