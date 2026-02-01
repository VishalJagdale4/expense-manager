package dev.vishal.expensemanager.service;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.vishal.expensemanager.dto.CategoryDto;
import dev.vishal.expensemanager.entity.Category;

import java.util.List;

public interface CategoryService {

    Category createCategory(CategoryDto dto) throws BadRequestException;

    Category updateCategory(CategoryDto dto) throws BadRequestException;

    Category getCategory(Long id) throws BadRequestException;

    List<Category> getCategoryByParent(Long id) throws BadRequestException;

    List<Category> getAllCategories();

    void deleteCategory(Long id) throws BadRequestException;
}
