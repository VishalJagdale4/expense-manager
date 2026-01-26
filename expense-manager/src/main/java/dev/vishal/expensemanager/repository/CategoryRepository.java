package dev.vishal.expensemanager.repository;

import dev.vishal.expensemanager.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByIdAndDeletedFalse(Long id);

    List<Category> findByNameAndParentCategoryIdAndDeletedFalse(String name, Long parentCategoryId);

    List<Category> findByIdNotAndNameAndParentCategoryIdAndDeletedFalse(Long id, String name, Long parentCategoryId);

    List<Category> findByParentCategoryIdAndDeletedFalseOrderByName(Long parentCategoryId);

    List<Category> findByDeletedFalseOrderByName();
}
