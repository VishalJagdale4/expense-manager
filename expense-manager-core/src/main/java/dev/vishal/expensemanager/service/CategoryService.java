package dev.vishal.expensemanager.service;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.vishal.expensemanager.dto.CategoryDto;
import dev.vishal.expensemanager.entity.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    Category createCategory(CategoryDto dto) throws BadRequestException;

    Category updateCategory(CategoryDto dto) throws BadRequestException;

    Category getCategory(Long id, UUID userId) throws BadRequestException;

    List<Category> getCategoryByParent(Long id, UUID userId) throws BadRequestException;

    List<Category> getAllCategories(UUID userId);

    void deleteCategory(Long id, UUID userId) throws BadRequestException;
}
