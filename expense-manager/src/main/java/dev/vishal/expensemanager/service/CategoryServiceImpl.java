package dev.vishal.expensemanager.service;

import dev.vishal.expensemanager.common.exception.BadRequestException;
import dev.vishal.expensemanager.dto.CategoryDto;
import dev.vishal.expensemanager.entity.Category;
import dev.vishal.expensemanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryDto dto) throws BadRequestException {

        if (Objects.nonNull(dto.getParentCategoryId())) {
            categoryRepository.findById(dto.getParentCategoryId())
                    .orElseThrow(() -> new BadRequestException("Parent Category not found"));
        }

        List<Category> existing =
                categoryRepository.findByNameAndParentCategoryIdAndDeletedFalse(
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
    public Category getCategory(Long id) throws BadRequestException {
        return categoryRepository.findById(id)
                .filter(c -> !c.getDeleted())
                .orElseThrow(() -> new BadRequestException("Category not found"));
    }

    @Override
    public List<Category> getCategoryByParent(Long id) throws BadRequestException {
        return categoryRepository.findByParentCategoryIdAndDeletedFalseOrderByName(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findByDeletedFalseOrderByName();
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) throws BadRequestException {
        Category category = categoryRepository.findByIdAndDeletedFalse(id);

        if (Objects.isNull(category)) {
            throw new BadRequestException("Category not found");
        }

        category.setDeleted(true);

        // All child Categories
        List<Category> categories = categoryRepository.findByParentCategoryIdAndDeletedFalseOrderByName(id);
        categories.forEach(c -> c.setDeleted(true));

        categories.add(category);

        categoryRepository.saveAll(categories);
    }

    @Override
    @Transactional
    public Category updateCategory(CategoryDto dto) throws BadRequestException {

        if (Objects.nonNull(dto.getParentCategoryId())) {
            categoryRepository.findById(dto.getParentCategoryId())
                    .orElseThrow(() -> new BadRequestException("Parent Category not found"));
        }

        Category existing = categoryRepository.findById(dto.getId())
                .filter(category -> !category.getDeleted())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        List<Category> existingDuplicates =
                categoryRepository.findByIdNotAndNameAndParentCategoryIdAndDeletedFalse(
                        dto.getId(),
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
        entity.setName(dto.getName());
        entity.setParentCategoryId(dto.getParentCategoryId());
    }

}
