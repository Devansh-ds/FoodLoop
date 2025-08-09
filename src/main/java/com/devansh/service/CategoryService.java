package com.devansh.service;

import com.devansh.model.Category;

import java.util.List;

public interface CategoryService {

    Category createCategory(String name);
    Category updateCategory(String newName, Integer categoryId);
    void deleteCategory(Integer categoryId);
    List<Category> getAllCategories();
    Category getCategoryById(Integer categoryId);
}
