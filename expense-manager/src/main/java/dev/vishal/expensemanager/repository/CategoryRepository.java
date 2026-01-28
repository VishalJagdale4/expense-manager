package dev.vishal.expensemanager.repository;

import dev.vishal.expensemanager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByIdAndIsDeletedFalse(Long id);

    List<Category> findByNameAndParentCategoryIdAndIsDeletedFalse(String name, Long parentCategoryId);

    List<Category> findByIdNotAndNameAndParentCategoryIdAndIsDeletedFalse(Long id, String name, Long parentCategoryId);

    List<Category> findByParentCategoryIdAndIsDeletedFalseOrderByName(Long parentCategoryId);

    List<Category> findByIsDeletedFalseOrderByName();
}
