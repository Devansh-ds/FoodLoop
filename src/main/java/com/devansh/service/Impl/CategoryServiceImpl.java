package com.devansh.service.Impl;

import com.devansh.exception.CategoryException;
import com.devansh.model.Category;
import com.devansh.repo.CategoryRepository;
import com.devansh.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(String name) {
        Category category = Category.builder().name(name).build();
        Optional<Category> savedCategory = categoryRepository.findByNameIgnoreCase(name);

        return savedCategory.orElseGet(() -> categoryRepository.save(category));
    }

    @Override
    public Category updateCategory(String newName, Integer categoryId) {
        Category prevCategory = getCategoryById(categoryId);
        prevCategory.setName(newName);
        return categoryRepository.save(prevCategory);
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        getCategoryById(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Integer categoryId) {
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new CategoryException("Category not found with id: " + categoryId));
    }
}
