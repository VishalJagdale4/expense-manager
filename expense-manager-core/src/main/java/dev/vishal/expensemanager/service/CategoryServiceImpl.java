package dev.vishal.expensemanager.service;

import dev.common.exceptionutils.exceptions.BadRequestException;
import dev.vishal.expensemanager.dto.CategoryDto;
import dev.vishal.expensemanager.entity.Category;
import dev.vishal.expensemanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryDto dto) throws BadRequestException {

        Long parentId = dto.getParentCategoryId();

        if (parentId != null) {
            Category parent = categoryRepository.findById(parentId)
                    .filter(c -> c.getUserId().equals(dto.getUserId()))
                    .filter(c -> !c.getIsDeleted())
                    .orElseThrow(() -> new BadRequestException("Parent category not found or deleted"));

            if (parent.getParentCategoryId() != null) {
                throw new BadRequestException("Sub-sub category not allowed");
            }
        }

        List<Category> existing =
                categoryRepository.findByUserIdAndNameAndParentCategoryIdAndIsDeletedFalse(
                        dto.getUserId(),
                        dto.getName(),
                        dto.getParentCategoryId()
                );

        if (!CollectionUtils.isEmpty(existing)) {
            throw new BadRequestException("Category already exists!");
        }

        Category category = new Category();
        copyDtoToEntity(dto, category);
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategory(Long id, UUID userId) throws BadRequestException {
        return categoryRepository.findById(id)
                .filter(c -> c.getUserId().equals(userId))
                .filter(c -> !c.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("Category not found"));
    }

    @Override
    public List<Category> getCategoryByParent(Long id, UUID userId) throws BadRequestException {
        return categoryRepository.findByParentCategoryIdAndUserIdAndIsDeletedFalseOrderByName(id, userId);
    }

    @Override
    public List<Category> getAllCategories(UUID userId) {
        return categoryRepository.findByUserIdAndIsDeletedFalseOrderByName(userId);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id, UUID userId) throws BadRequestException {
        Category category = categoryRepository.findByIdAndUserIdAndIsDeletedFalse(id, userId);

        if (Objects.isNull(category)) {
            throw new BadRequestException("Category not found");
        }

        category.setIsDeleted(true);

        // All child Categories
        List<Category> categories =
                categoryRepository.findByParentCategoryIdAndUserIdAndIsDeletedFalseOrderByName(id, userId);
        categories.forEach(c -> c.setIsDeleted(true));

        categories.add(category);

        categoryRepository.saveAll(categories);
    }

    @Override
    @Transactional
    public Category updateCategory(CategoryDto dto) throws BadRequestException {

        if (Objects.nonNull(dto.getParentCategoryId())) {
            categoryRepository.findById(dto.getParentCategoryId())
                    .filter(category -> category.getUserId().equals(dto.getUserId()))
                    .orElseThrow(() -> new BadRequestException("Parent Category not found"));
        }

        Category existing = categoryRepository.findById(dto.getId())
                .filter(category -> category.getUserId().equals(dto.getUserId()))
                .filter(category -> !category.getIsDeleted())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        List<Category> existingDuplicates =
                categoryRepository.findByIdNotAndUserIdAndNameAndParentCategoryIdAndIsDeletedFalse(
                        dto.getId(),
                        dto.getUserId(),
                        dto.getName(),
                        dto.getParentCategoryId()
                );

        if (!CollectionUtils.isEmpty(existingDuplicates)) {
            throw new BadRequestException("Category already exists!");
        }

        copyDtoToEntity(dto, existing);
        return categoryRepository.save(existing);
    }

    // ------------------ Helper methods ------------------

    private void copyDtoToEntity(CategoryDto dto, Category entity) {
        entity.setUserId(dto.getUserId());
        entity.setName(dto.getName());
        entity.setParentCategoryId(dto.getParentCategoryId());
    }

}
