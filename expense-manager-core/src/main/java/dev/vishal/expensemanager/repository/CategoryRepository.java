package dev.vishal.expensemanager.repository;

import dev.vishal.expensemanager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByIdAndUserIdAndIsDeletedFalse(Long id, UUID userId);

    List<Category> findByUserIdAndNameAndParentCategoryIdAndIsDeletedFalse(UUID userId, String name, Long parentCategoryId);

    List<Category> findByIdNotAndUserIdAndNameAndParentCategoryIdAndIsDeletedFalse(Long id, UUID userId, String name, Long parentCategoryId);

    List<Category> findByParentCategoryIdAndUserIdAndIsDeletedFalseOrderByName(Long parentCategoryId, UUID userId);

    List<Category> findByUserIdAndIsDeletedFalseOrderByName(UUID userId);
}
